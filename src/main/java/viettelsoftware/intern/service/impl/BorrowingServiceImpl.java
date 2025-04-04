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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.BorrowedBookInfo;
import viettelsoftware.intern.dto.request.EmailObjectRequest;
import viettelsoftware.intern.dto.response.BorrowingResponse;
import viettelsoftware.intern.entity.*;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.mapper.BorrowingMapper;
import viettelsoftware.intern.repository.BookRepository;
import viettelsoftware.intern.repository.BorrowingBookRepository;
import viettelsoftware.intern.repository.BorrowingRepository;
import viettelsoftware.intern.repository.UserRepository;
import viettelsoftware.intern.service.BorrowingService;
import viettelsoftware.intern.util.EmailUtil;
import viettelsoftware.intern.util.SecurityUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
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
    BorrowingBookRepository borrowingBookRepository;
    UserRepository userRepository;
    BookRepository bookRepository;
    BorrowingMapper borrowingMapper;
    EmailUtil emailUtil;

    @Override
    public BorrowingResponse create(String userId, Set<String> bookIds) {
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        BorrowingEntity borrowing = BorrowingEntity.builder()
                .user(user)
                .borrowedAt(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .build();
        borrowing = borrowingRepository.save(borrowing);
        BorrowingEntity finalBorrowing = borrowing;
        Set<BorrowingBook> sets = bookIds.stream().map(bookId -> {
            BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            return BorrowingBook.builder()
                    .borrowing(finalBorrowing)
                    .book(book)
                    .build();
        }).collect(Collectors.toSet());
        borrowingBookRepository.saveAll(sets);
        borrowing.setBorrowings(sets);
        return borrowingMapper.toBorrowingResponse(borrowing);
    }

    @Override
    public BorrowingResponse update(String borrowingId, Set<String> bookIds) {
        BorrowingEntity borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROWING_NOT_FOUND));

        Set<BorrowingBook> updatedBorrowings = bookIds.stream().map(bookId -> {
            BookEntity book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            return BorrowingBook.builder()
                    .borrowing(borrowing)
                    .book(book)
                    .build();
        }).collect(Collectors.toSet());

        borrowing.setBorrowings(updatedBorrowings);
        return borrowingMapper.toBorrowingResponse(borrowingRepository.save(borrowing));
    }

    @Override
    public void delete(String permissionId) {
        if (!borrowingRepository.existsById(permissionId))
            throw new AppException(ErrorCode.BORROWING_NOT_FOUND);
        borrowingRepository.deleteById(permissionId);
    }

    @Override
    public BorrowingResponse getBorrowing(String borrowingId) {
        BorrowingEntity borrowing = borrowingRepository.findById(borrowingId).orElseThrow(
                () -> new AppException(ErrorCode.BORROWING_NOT_FOUND));
        return borrowingMapper.toBorrowingResponse(borrowing);
    }

    @Override
    public Page<BorrowingResponse> getAllBorrowing(Pageable pageable) {
        return borrowingRepository.findAll(pageable).map(borrowingMapper::toBorrowingResponse);
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

    @Transactional
    public int sendReminderEmails() {
        LocalDate reminderDate = LocalDate.now().plusDays(2);
        List<BorrowingEntity> borrowings = borrowingRepository.findByDueDate(reminderDate);

        if (borrowings.isEmpty()) {
            log.info("Không có người dùng nào cần nhận email nhắc nhở.");
            return 0;
        }

        int count = 0;
        for (BorrowingEntity borrowing : borrowings) {
            String email = borrowing.getUser().getEmail();
            String subject = "Thông báo sắp đến hạn trả sách";
            Map<String, Object> params = new HashMap<>();
            params.put("username", borrowing.getUser().getUsername());
            params.put("dueDate", borrowing.getDueDate().toString());
            params.put("books", borrowing.getBorrowings().stream()
                    .map(b -> b.getBook().getTitle())
                    .collect(Collectors.toList()));

            EmailObjectRequest emailObjectRequest = EmailObjectRequest.builder()
                    .emailTo(new String[]{email})
                    .subject(subject)
                    .template("email-reminder")
                    .params(params)
                    .build();

            emailUtil.sendEmail(emailObjectRequest);
            log.info("Đã gửi email nhắc nhở đến {}", email);
            count++;
        }

        return count;
    }

    @Override
    public List<BorrowedBookInfo> getBorrowedBooksByCurrentUser() {
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        log.info("Current user: {}", username);

        UserEntity user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Lấy danh sách các BorrowingEntity chưa trả
        List<BorrowingEntity> activeBorrowings = borrowingRepository.findByUserAndReturnedAtIsNull(user);
        log.info("Active borrowings: {}", activeBorrowings.size());

        for (BorrowingEntity b : activeBorrowings) {
            log.info("Borrowing ID: {}", b.getBorrowingId());
            log.info("BorrowingBooks size: {}", b.getBorrowings().size());
        }

        // Lấy tất cả BorrowingBook liên quan đến các Borrowing chưa trả
        return activeBorrowings.stream()
                .flatMap(borrowing -> borrowing.getBorrowings().stream())
                .map(borrowingBook -> new BorrowedBookInfo(
                        borrowingBook.getBook().getTitle(),
                        borrowingBook.getQuantity()
                ))
                .collect(Collectors.toList());
    }
}
