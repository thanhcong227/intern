package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
import viettelsoftware.intern.dto.request.BookRequest;
import viettelsoftware.intern.dto.response.BookResponse;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.service.impl.BookServiceImpl;

@RestController
@RequestMapping("/book")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {

    BookServiceImpl bookServiceImpl;
    private final ResponseFactory responseFactory;

    @PostMapping()
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    ResponseEntity<GeneralResponse<BookResponse>> create(@RequestBody BookRequest request) {
        return responseFactory.success(bookServiceImpl.create(request));
    }

    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    ResponseEntity<GeneralResponse<BookResponse>> update(@PathVariable String bookId, @RequestBody BookRequest request) {
        return responseFactory.success(bookServiceImpl.update(bookId, request));
    }

    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('BOOK_MANAGE')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String bookId) {
        bookServiceImpl.delete(bookId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{bookId}")
    @PreAuthorize("hasAuthority('BOOK_VIEW')")
    ResponseEntity<GeneralResponse<BookResponse>> getBook(@PathVariable String bookId) {
        return responseFactory.success(bookServiceImpl.getBook(bookId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BOOK_VIEW')")
    ResponseEntity<GeneralResponse<Page<BookResponse>>> getAllBook(@PageableDefault(size = 9) Pageable pageable) {
        return responseFactory.success(bookServiceImpl.getAllBooks(pageable));
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

    @GetMapping("/search")
    public ResponseEntity<GeneralResponse<Page<BookResponse>>> searchBooks(
            @RequestParam("query") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> result = bookServiceImpl.searchBooks(query, pageable);

        return responseFactory.success(result);
    }
}
