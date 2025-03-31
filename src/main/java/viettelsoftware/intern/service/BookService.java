package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.BookRequest;
import viettelsoftware.intern.dto.response.BookResponse;

public interface BookService {
    BookResponse create(BookRequest request);

    BookResponse update(String bookId, BookRequest request);

    void delete(String bookId);
    BookResponse getBook(String bookId);
    Page<BookResponse> getAllBooks(Pageable pageable);
    byte[] exportBooksToExcel();
}
