package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.BorrowingEntity;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity,String> {

    boolean existsByBookId(String borrowingId);
    boolean existsByTitle(String name);
    Optional<BookEntity> findByTitle(String title);
}
