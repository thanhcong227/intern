package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.CommentEntity;
import viettelsoftware.intern.entity.GenreEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity,String> {

    boolean existsByName(String name);

    Optional<GenreEntity> findByName(String name);

    @Query("SELECT g.name AS genreName, COUNT(b) AS bookCount " +
            "FROM GenreEntity g " +
            "JOIN g.books b " +
            "GROUP BY g.name")
    List<Object[]> countBooksByGenre();
}
