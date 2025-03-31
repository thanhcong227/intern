package viettelsoftware.intern.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import viettelsoftware.intern.entity.BorrowingEntity;
import viettelsoftware.intern.entity.BorrowingBook;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

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
    Set<String> borrowingBookIds;
}
