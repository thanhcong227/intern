package viettelsoftware.intern.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowingRequest {
    private String userId;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
    private LocalDate returnedAt;
    private List<BorrowedBookItem> borrowedBooks;

    @Data
    public static class BorrowedBookItem {
        private String bookId;
        private int quantity;
    }
}