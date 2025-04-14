package viettelsoftware.intern.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "borrowings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String borrowingId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    UserEntity user;
    LocalDate borrowedAt;
    LocalDate dueDate;
    LocalDate returnedAt;

    @OneToMany(mappedBy = "borrowing", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BorrowedBookDetail> borrowedBooks;
}
