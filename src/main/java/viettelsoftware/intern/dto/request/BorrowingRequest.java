package viettelsoftware.intern.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowingRequest {

    String userId;
    LocalDate borrowedAt;
    LocalDate dueDate;
    LocalDate returnedAt;
    private Set<String> bookIds;
}
