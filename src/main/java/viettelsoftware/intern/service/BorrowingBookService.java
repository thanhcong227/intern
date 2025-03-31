package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.BorrowingBookRequest;
import viettelsoftware.intern.dto.response.BorrowingBookResponse;

import java.util.List;

public interface BorrowingBookService {
    BorrowingBookResponse create(String borrowingId, List<BorrowingBookRequest> request);

    BorrowingBookResponse update(String borrowingBookId, BorrowingBookRequest request);

    void delete(String borrowingBookId);
    BorrowingBookResponse getBorrowingBook(String borrowingBookId);
    Page<BorrowingBookResponse> getAllBorrowingBooks(Pageable pageable);
}
