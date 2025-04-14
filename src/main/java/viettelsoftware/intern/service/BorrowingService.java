package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.BorrowedBookInfo;
import viettelsoftware.intern.dto.request.BorrowingRequest;
import viettelsoftware.intern.dto.response.BorrowingResponse;

import java.util.List;
import java.util.Set;

public interface BorrowingService {
    BorrowingResponse create(BorrowingRequest request);

    BorrowingResponse update(String borrowingId, Set<BorrowingRequest.BorrowedBookItem> updatedBooks);

    void delete(String borrowingId);
    BorrowingResponse getBorrowing(String borrowingId);
    Page<BorrowingResponse> getAllBorrowing(Pageable pageable);
    byte[] exportBorrowingsToExcel();
    int scheduleReminderEmails();
    List<BorrowedBookInfo> getBorrowedBooksByCurrentUser();
}
