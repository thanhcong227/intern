package viettelsoftware.intern.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.BorrowingEntity;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity,String> {

    boolean existsByBookId(String borrowingId);
    boolean existsByTitle(String name);
    Optional<BookEntity> findByTitle(String title);

    @Query("""
            SELECT DISTINCT b FROM BookEntity b
            LEFT JOIN b.genres g
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))
               OR STR(b.year) LIKE CONCAT('%', :query, '%')
               OR LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<BookEntity> searchBooks(@Param("query") String query, Pageable pageable);
}
