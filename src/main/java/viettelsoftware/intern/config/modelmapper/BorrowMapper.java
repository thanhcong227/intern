package viettelsoftware.intern.config.modelmapper;

import org.springframework.stereotype.Component;
import viettelsoftware.intern.dto.response.BorrowingResponse;
import viettelsoftware.intern.entity.BorrowingEntity;

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
        dto.setBorrowingBookIds(borrowing.getBorrowedBooks().stream().map(book -> book.getBook().getBookId()).collect(Collectors.toSet()));
        return dto;
    }
}
