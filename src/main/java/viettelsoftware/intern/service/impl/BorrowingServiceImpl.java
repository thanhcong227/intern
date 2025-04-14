package viettelsoftware.intern.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import viettelsoftware.intern.config.modelmapper.BorrowMapper;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.dto.BorrowedBookInfo;
import viettelsoftware.intern.dto.request.BorrowingRequest;
import viettelsoftware.intern.dto.response.BorrowingResponse;
import viettelsoftware.intern.entity.*;
import viettelsoftware.intern.exception.CustomException;
import viettelsoftware.intern.repository.BookRepository;
import viettelsoftware.intern.repository.BorrowedBookDetailRepository;
import viettelsoftware.intern.repository.BorrowingRepository;
import viettelsoftware.intern.repository.EmailReminderRepository;
import viettelsoftware.intern.repository.UserRepository;
import viettelsoftware.intern.service.BorrowingService;
import viettelsoftware.intern.util.SecurityUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BorrowingServiceImpl implements BorrowingService {

    BorrowingRepository borrowingRepository;
    UserRepository userRepository;
    BookRepository bookRepository;
    ModelMapper modelMapper;
    EmailReminderRepository emailReminderRepository;
    BorrowedBookDetailRepository borrowedBookDetailRepository;
    BorrowMapper borrowMapper;

    @Override
    @Transactional
    public BorrowingResponse create(BorrowingRequest request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND));

        BorrowingEntity borrowing = BorrowingEntity.builder()
                .user(user)
                .borrowedAt(request.getBorrowedAt() != null ? request.getBorrowedAt() : LocalDate.now())
                .dueDate(request.getDueDate() != null ? request.getDueDate() : LocalDate.now().plusDays(14))
                .returnedAt(request.getReturnedAt())
                .build();

        borrowing = borrowingRepository.save(borrowing); // lưu trước để lấy ID

        List<BookEntity> booksToUpdate = new ArrayList<>();
        Set<BorrowedBookDetail> borrowedBookDetails = new HashSet<>();

        for (BorrowingRequest.BorrowedBookItem borrowedBook : request.getBorrowedBooks()) {
            BookEntity book = bookRepository.findById(borrowedBook.getBookId())
                    .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.BOOK_NOT_FOUND));

            if (book.getAvailableQuantity() < borrowedBook.getQuantity()) {
                throw new CustomException(ResponseStatusCodeEnum.BOOK_OUT_OF_STOCK);
            }

            book.setAvailableQuantity(book.getAvailableQuantity() - borrowedBook.getQuantity());
            booksToUpdate.add(book);

            BorrowedBookDetail detail = BorrowedBookDetail.builder()
                    .borrowing(borrowing)
                    .book(book)
                    .quantity(borrowedBook.getQuantity())
                    .build();

            borrowedBookDetails.add(detail);
        }

        bookRepository.saveAll(booksToUpdate);
        borrowedBookDetailRepository.saveAll(borrowedBookDetails);
        borrowing.setBorrowedBooks(borrowedBookDetails);

        return borrowMapper.toDto(borrowing);
    }

    @Override
    @Transactional
    public BorrowingResponse update(String borrowingId, Set<BorrowingRequest.BorrowedBookItem> updatedBooks) {
        BorrowingEntity borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.BORROWING_NOT_FOUND));

        // 1. Trả lại số lượng sách cũ
        Set<BorrowedBookDetail> oldDetails = borrowing.getBorrowedBooks();
        for (BorrowedBookDetail oldDetail : oldDetails) {
            BookEntity book = oldDetail.getBook();
            book.setAvailableQuantity(book.getAvailableQuantity() + oldDetail.getQuantity());
            bookRepository.save(book);
        }

        // 2. Xóa toàn bộ chi tiết mượn cũ
        borrowedBookDetailRepository.deleteAll(oldDetails);
        borrowing.getBorrowedBooks().clear();

        // 3. Tạo danh sách mới
        Set<BorrowedBookDetail> newDetails = new HashSet<>();
        List<BookEntity> booksToUpdate = new ArrayList<>();

        for (BorrowingRequest.BorrowedBookItem item : updatedBooks) {
            BookEntity book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.BOOK_NOT_FOUND));

            if (book.getAvailableQuantity() < item.getQuantity()) {
                throw new CustomException(ResponseStatusCodeEnum.BOOK_OUT_OF_STOCK);
            }

            // Trừ số lượng sách mới
            book.setAvailableQuantity(book.getAvailableQuantity() - item.getQuantity());
            booksToUpdate.add(book);

            BorrowedBookDetail detail = BorrowedBookDetail.builder()
                    .borrowing(borrowing)
                    .book(book)
                    .quantity(item.getQuantity())
                    .build();

            newDetails.add(detail);
        }

        bookRepository.saveAll(booksToUpdate);
        borrowedBookDetailRepository.saveAll(newDetails);
        borrowing.setBorrowedBooks(newDetails);

        return modelMapper.map(borrowing, BorrowingResponse.class);
    }

    @Override
    public void delete(String borrowingId) {
        if (!borrowingRepository.existsById(borrowingId))
            throw new CustomException(ResponseStatusCodeEnum.BORROWING_NOT_FOUND);
        borrowingRepository.deleteById(borrowingId);
    }

    @Override
    public BorrowingResponse getBorrowing(String borrowingId) {
        BorrowingEntity borrowing = borrowingRepository.findById(borrowingId).orElseThrow(
                () -> new CustomException(ResponseStatusCodeEnum.BORROWING_NOT_FOUND));
        return modelMapper.map(borrowing, BorrowingResponse.class);
    }

    @Override
    public Page<BorrowingResponse> getAllBorrowing(Pageable pageable) {
        Page<BorrowingEntity> borrowings = borrowingRepository.findAll(pageable);
        return borrowings.map(borrowing -> {
            BorrowingResponse response = modelMapper.map(borrowing, BorrowingResponse.class);
            response.setUsers(borrowing.getUser().getFullName());
            return response;
        });
    }

    @Override
    public byte[] exportBorrowingsToExcel() {
        try {
            List<BorrowingEntity> borrowings = borrowingRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Borrowings");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "User", "BorrowedAt", "DueDate", "ReturnAt"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (BorrowingEntity borrowing : borrowings) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(borrowing.getBorrowingId());
                row.createCell(1).setCellValue(borrowing.getUser().getFullName());
                row.createCell(2).setCellValue(String.valueOf(borrowing.getBorrowedAt()));
                row.createCell(3).setCellValue(String.valueOf(borrowing.getDueDate()));
                row.createCell(4).setCellValue(String.valueOf(borrowing.getReturnedAt()));
            }

            String filePath = "borrowings.xlsx";
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowedBookInfo> getBorrowedBooksByCurrentUser() {
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND));
        log.info("Current user: {}", username);

        UserEntity user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND));

        // Lấy các Borrowing chưa trả
        List<BorrowingEntity> activeBorrowings = borrowingRepository.findByUserAndReturnedAtIsNull(user);
        log.info("Active borrowings: {}", activeBorrowings.size());

        // Gom nhóm sách theo tiêu đề
        Map<String, List<BorrowedBookDetail>> groupedByTitle = activeBorrowings.stream()
                .flatMap(b -> b.getBorrowedBooks().stream())
                .collect(Collectors.groupingBy(bd -> bd.getBook().getTitle()));

        return groupedByTitle.entrySet().stream()
                .map(entry -> {
                    String title = entry.getKey();
                    List<BorrowedBookDetail> details = entry.getValue();

                    int totalQuantity = details.stream()
                            .mapToInt(BorrowedBookDetail::getQuantity)
                            .sum();

                    LocalDate earliestBorrowedAt = details.stream()
                            .map(bd -> bd.getBorrowing().getBorrowedAt())
                            .min(LocalDate::compareTo)
                            .orElse(null);

                    LocalDate latestDueDate = details.stream()
                            .map(bd -> bd.getBorrowing().getDueDate())
                            .max(LocalDate::compareTo)
                            .orElse(null);

                    return new BorrowedBookInfo(title, totalQuantity, earliestBorrowedAt, latestDueDate);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int scheduleReminderEmails() {
        LocalDate reminderDate = LocalDate.now().plusDays(2);
        List<BorrowingEntity> borrowings = borrowingRepository.findByDueDate(reminderDate);

        if (borrowings.isEmpty()) {
            log.info("Không có người dùng nào cần nhận email nhắc nhở.");
            return 0;
        }

        int count = 0;
        for (BorrowingEntity borrowing : borrowings) {
            UserEntity user = borrowing.getUser();
            String email = user.getEmail();
            String subject = "Thông báo sắp đến hạn trả sách";

            List<String> bookTitles = borrowing.getBorrowedBooks().stream()
                    .map(detail -> detail.getBook().getTitle())
                    .toList();

            EmailReminder emailReminder = EmailReminder.builder()
                    .email(email)
                    .subject(subject)
                    .username(user.getUsername())
                    .dueDate(borrowing.getDueDate().toString())
                    .bookTitles(bookTitles)
                    .scheduledTime(LocalDateTime.now().plusMinutes(5))
                    .sent(false)
                    .build();

            emailReminderRepository.save(emailReminder);
            log.info("Đã lưu email nhắc nhở cho {}", email);
            count++;
        }

        return count;
    }
}
