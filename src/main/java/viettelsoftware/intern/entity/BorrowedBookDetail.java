package viettelsoftware.intern.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Table(name = "BORROWED_BOOK_DETAILS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowedBookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String borrowedBookDetailId;

    @ManyToOne
    @JoinColumn(name = "bookId", nullable = false)
    BookEntity book;

    @ManyToOne
    @JoinColumn(name = "borrowingId", nullable = false)
    BorrowingEntity borrowing;

    int quantity;
}
