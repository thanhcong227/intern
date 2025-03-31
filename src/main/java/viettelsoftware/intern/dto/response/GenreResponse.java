package viettelsoftware.intern.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import viettelsoftware.intern.entity.GenreEntity;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreResponse {

    String genreId;
    String name;
    LocalDate createdAt;
    Set<String> books;

}
