package viettelsoftware.intern.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import viettelsoftware.intern.entity.GenreEntity;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookRequest {

    String title;
    String author;
    int year;

    Set<BorrowingRequest> borrowings;
    Set<GenreRequest> genres;
}
