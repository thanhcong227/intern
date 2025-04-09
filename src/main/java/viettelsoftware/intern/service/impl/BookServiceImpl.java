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
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.dto.request.BookRequest;
import viettelsoftware.intern.dto.response.BookResponse;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.GenreEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.exception.CustomException;
import viettelsoftware.intern.mapper.BookMapper;
import viettelsoftware.intern.repository.BookRepository;
import viettelsoftware.intern.repository.GenreRepository;
import viettelsoftware.intern.service.BookService;
import viettelsoftware.intern.util.UpdateHelper;

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
public class BookServiceImpl implements BookService {

    BookRepository bookRepository;
    GenreRepository genreRepository;
    BookMapper bookMapper;
    private final ModelMapper modelMapper;

    @Override
    public BookResponse create(BookRequest request) {
        if (bookRepository.existsByTitle(request.getTitle()))
            throw new CustomException(ResponseStatusCodeEnum.BOOK_EXISTED);

        Set<GenreEntity> setGenre = request.getGenres().stream().map(genreEntity -> genreRepository.findByName(genreEntity.getName()).orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.GENRE_NOT_FOUND))).collect(Collectors.toSet());

        BookEntity book = BookEntity.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .year(request.getYear())
                .quantity(request.getQuantity())
                .availableQuantity(request.getQuantity())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .genres(setGenre)
                .build();
        return bookMapper.toBookResponse(bookRepository.save(book));
    }

    @Override
    public BookResponse update(String bookId, BookRequest request) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.BOOK_NOT_FOUND));

        if (request.getTitle() != null &&
                !book.getTitle().equals(request.getTitle()) &&
                bookRepository.existsByTitle(request.getTitle())) {
            throw new CustomException(ResponseStatusCodeEnum.BOOK_EXISTED);
        }

        UpdateHelper.updateIfNotNull(book::setTitle, request.getTitle());
        UpdateHelper.updateIfNotNull(book::setAuthor, request.getAuthor());
        UpdateHelper.updateIfPositive(book::setYear, request.getYear());
        UpdateHelper.updateIfNonNegative(book::setQuantity, request.getQuantity());
        UpdateHelper.updateIfNonNegative(book::setAvailableQuantity, request.getAvailableQuantity());

        if (request.getGenres() != null) {
            Set<GenreEntity> setGenre = request.getGenres().stream()
                    .map(genre -> genreRepository.findByName(genre.getName())
                            .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.GENRE_NOT_FOUND)))
                    .collect(Collectors.toSet());

            book.setGenres(setGenre);
        }

        book.setUpdatedAt(LocalDate.now());
        return modelMapper.map(bookRepository.save(book), BookResponse.class);
    }




    @Override
    public void delete(String bookId) {
        if (!bookRepository.existsById(bookId))
            throw new CustomException(ResponseStatusCodeEnum.BOOK_NOT_FOUND);
        bookRepository.deleteById(bookId);
    }

    @Override
    public BookResponse getBook(String bookId) {
        BookEntity book = bookRepository.findById(bookId).orElseThrow(
                () -> new CustomException(ResponseStatusCodeEnum.BOOK_NOT_FOUND));

        return bookMapper.toBookResponse(book);
    }

    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(bookMapper::toBookResponse);
    }

    @Override
    public byte[] exportBooksToExcel() {
        try {
            List<BookEntity> books = bookRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Books");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Title", "Author", "Year", "Created At", "Updated At"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (BookEntity bookEntity : books) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(bookEntity.getBookId());
                row.createCell(1).setCellValue(bookEntity.getTitle());
                row.createCell(2).setCellValue(bookEntity.getAuthor());
                row.createCell(3).setCellValue(bookEntity.getYear());
                row.createCell(4).setCellValue(bookEntity.getCreatedAt().toString());
                row.createCell(5).setCellValue(bookEntity.getUpdatedAt().toString());
            }

            String filePath = "books.xlsx";
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
