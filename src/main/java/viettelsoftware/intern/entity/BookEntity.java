package viettelsoftware.intern.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String bookId;
    String title;
    String author;
    int year;
    LocalDate createdAt;
    LocalDate updatedAt;
    int quantity;
    int availableQuantity;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    Set<BorrowingBook> borrowingBooks;

    @ManyToMany()
    @JoinTable(
            name = "book_genre",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<GenreEntity> genres;
}
