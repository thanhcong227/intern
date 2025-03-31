package viettelsoftware.intern.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowingBookResponse {

    String borrowingBookId;
    String bookTitle;
    int quantity;
    String borrowingId;
}
