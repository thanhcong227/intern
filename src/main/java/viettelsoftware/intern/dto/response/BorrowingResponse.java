package viettelsoftware.intern.dto.response;

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
public class BorrowingResponse {

    String borrowingId;
    String users;
    LocalDate borrowedAt;
    LocalDate dueDate;
    LocalDate returnedAt;
    private List<BookDetail> borrowedBooks;
    @Data
    public static class BookDetail {
        private String bookId;
        private String title;
        private int quantity;
    }
}
