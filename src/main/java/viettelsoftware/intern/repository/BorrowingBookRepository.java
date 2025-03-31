package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.BorrowingBook;
import viettelsoftware.intern.entity.BorrowingEntity;

@Repository
public interface BorrowingBookRepository extends JpaRepository<BorrowingBook,String> {

    boolean existsByBorrowingBookId(String borrowingId);
}
