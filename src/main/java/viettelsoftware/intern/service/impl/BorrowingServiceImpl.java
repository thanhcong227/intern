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
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.response.BorrowingResponse;
import viettelsoftware.intern.entity.*;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.mapper.BorrowingMapper;
import viettelsoftware.intern.repository.BookRepository;
import viettelsoftware.intern.repository.BorrowingRepository;
import viettelsoftware.intern.repository.UserRepository;
import viettelsoftware.intern.service.BorrowingService;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
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
    BorrowingMapper borrowingMapper;

    @Override
    public BorrowingResponse create(String userId, Set<String> bookIds) {
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        BorrowingEntity borrowing = BorrowingEntity.builder()
                .user(user)
                .borrowedAt(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .build();

        Set<BorrowingBook> sets = bookIds.stream().map(bookId ->
        {
            BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));
            return BorrowingBook.builder()
                    .borrowing(borrowing)
                    .book(book)
                    .build();
        }).collect(Collectors.toSet());

        borrowing.setBorrowings(sets);
        return borrowingMapper.toBorrowingResponse(borrowingRepository.save(borrowing));
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
}
