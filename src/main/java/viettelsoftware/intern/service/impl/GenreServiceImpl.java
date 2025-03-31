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
import viettelsoftware.intern.dto.request.GenreRequest;
import viettelsoftware.intern.dto.response.GenreResponse;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.GenreEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.mapper.GenreMapper;
import viettelsoftware.intern.repository.BookRepository;
import viettelsoftware.intern.repository.GenreRepository;
import viettelsoftware.intern.service.GenreService;

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
public class GenreServiceImpl implements GenreService {

    GenreRepository genreRepository;
    GenreMapper genreMapper;
    BookRepository bookRepository;

    @Override
    public GenreResponse create(GenreRequest request) {
        if (genreRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.GENRE_EXISTED);
        Set<BookEntity> books = request.getBooks().stream().map(bookRequest -> bookRepository.findByTitle(bookRequest.getTitle()).orElseThrow(
                () -> new AppException(ErrorCode.BOOK_NOT_FOUND))).collect(Collectors.toSet());
        GenreEntity genreEntity = GenreEntity.builder()
                .name(request.getName())
                .books(books)
                .createdAt(LocalDate.now())
                .build();
        return genreMapper.toGenreResponse(genreRepository.save(genreEntity));
    }

    @Override
    public GenreResponse update(String genreId, GenreRequest request) {
        GenreEntity existingGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_FOUND));

        if (!existingGenre.getName().equals(request.getName()) && genreRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.GENRE_EXISTED);
        }

        Set<BookEntity> books = request.getBooks().stream()
                .map(bookRequest -> bookRepository.findByTitle(bookRequest.getTitle())
                        .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND)))
                .collect(Collectors.toSet());

        existingGenre.setName(request.getName());
        existingGenre.setBooks(books);
        existingGenre.setUpdatedAt(LocalDate.now());

        return genreMapper.toGenreResponse(genreRepository.save(existingGenre));
    }

    @Override
    public void delete(String genreId) {
        if (!genreRepository.existsById(genreId))
            throw new AppException(ErrorCode.GENRE_NOT_FOUND);
        genreRepository.deleteById(genreId);
    }

    @Override
    public GenreResponse getGenre(String genreId) {
        GenreEntity genreEntity = genreRepository.findById(genreId).orElseThrow(
                () -> new AppException(ErrorCode.GENRE_NOT_FOUND));
        return genreMapper.toGenreResponse(genreEntity);
    }

    @Override
    public Page<GenreResponse> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable).map(genreMapper::toGenreResponse);
    }

    @Override
    public byte[] exportGenresToExcel(){
        try {
            List<GenreEntity> genres = genreRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Posts");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name", "Created At"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (GenreEntity genreEntity : genres) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(genreEntity.getGenreId());
                row.createCell(1).setCellValue(genreEntity.getName());
                row.createCell(2).setCellValue(genreEntity.getCreatedAt().toString());
            }

            String filePath = "genres.xlsx";
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
