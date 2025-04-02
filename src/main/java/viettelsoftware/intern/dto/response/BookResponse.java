package viettelsoftware.intern.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.BorrowingBook;
import viettelsoftware.intern.entity.GenreEntity;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponse {

    String bookId;
    String title;
    String author;
    int year;
    int quantity;
    int availableQuantity;
    LocalDate createdAt;
    LocalDate updatedAt;

    Set<String> borrowingBooks;
    Set<String> genres;
}
