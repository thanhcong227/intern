package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.BorrowedBookInfo;
import viettelsoftware.intern.dto.response.BorrowingResponse;

import java.util.List;
import java.util.Set;

public interface BorrowingService {
    BorrowingResponse create(String userId, Set<String> bookIds);

    BorrowingResponse update(String borrowingId, Set<String> bookIds);

    void delete(String borrowingId);
    BorrowingResponse getBorrowing(String borrowingId);
    Page<BorrowingResponse> getAllBorrowing(Pageable pageable);
    byte[] exportBorrowingsToExcel();

    List<BorrowedBookInfo> getBorrowedBooksByCurrentUser();
}
