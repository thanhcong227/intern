package viettelsoftware.intern.config.modelmapper;

import org.springframework.stereotype.Component;
import viettelsoftware.intern.dto.response.BorrowingResponse;
import viettelsoftware.intern.entity.BorrowingEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BorrowMapper {

    public BorrowingResponse toDto(BorrowingEntity borrowing) {
        BorrowingResponse dto = new BorrowingResponse();
        dto.setBorrowingId(borrowing.getBorrowingId());
        dto.setUsers(borrowing.getUser().getUsername());
        dto.setBorrowedAt(borrowing.getBorrowedAt());
        dto.setDueDate(borrowing.getDueDate());
        dto.setReturnedAt(borrowing.getReturnedAt());

        List<BorrowingResponse.BookDetail> bookDetails = borrowing.getBorrowedBooks().stream()
                .map(detail -> {
                    BorrowingResponse.BookDetail bookDetail = new BorrowingResponse.BookDetail();
                    bookDetail.setBookId(detail.getBook().getBookId());
                    bookDetail.setTitle(detail.getBook().getTitle());
                    bookDetail.setQuantity(detail.getQuantity());
                    return bookDetail;
                })
                .collect(Collectors.toList());

        dto.setBorrowedBooks(bookDetails);

        return dto;
    }
}
