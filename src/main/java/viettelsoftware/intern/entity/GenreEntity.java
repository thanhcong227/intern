package viettelsoftware.intern.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "genres")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String genreId;
    String name;
    LocalDate createdAt;
    LocalDate updatedAt;

    @ManyToMany(mappedBy = "genres")
    Set<BookEntity> books;
}
