package viettelsoftware.intern.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "borrowing_books")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowingBook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String borrowingBookId;

    @ManyToOne
    @JoinColumn(name = "borrowingId", nullable = false)
    BorrowingEntity borrowing;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    BookEntity book;

    @Builder.Default
    int quantity = 1;

}
