package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.dto.request.BookRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.BookResponse;
import viettelsoftware.intern.service.impl.BookServiceImpl;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    BookServiceImpl bookServiceImpl;

    @PostMapping()
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    ApiResponse<BookResponse> create(@RequestBody BookRequest request) {
        return ApiResponse.<BookResponse>builder()
                .result(bookServiceImpl.create(request))
                .build();
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    ApiResponse<BookResponse> update(@PathVariable String bookId, @RequestBody BookRequest request) {
        return ApiResponse.<BookResponse>builder()
                .result(bookServiceImpl.update(bookId, request))
                .build();
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    ApiResponse<Void> delete(@PathVariable String bookId) {
        bookServiceImpl.delete(bookId);
        return ApiResponse.<Void>builder()
                .message("Book deleted successfully")
                .build();
    }

    @GetMapping("/{bookId}")
    @PreAuthorize("hasAuthority('BOOK_VIEW')")
    ApiResponse<BookResponse> getBook(@PathVariable String bookId) {
        return ApiResponse.<BookResponse>builder()
                .result(bookServiceImpl.getBook(bookId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BOOK_VIEW')")
    ApiResponse<Page<BookResponse>> getAllBook(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<BookResponse>>builder()
                .result(bookServiceImpl.getAllBooks(pageable))
                .build();
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
    public ResponseEntity<byte[]> exportBooksToExcel() {
        byte[] excelData = bookServiceImpl.exportBooksToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=books.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }
}
