package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.CommentEntity;
import viettelsoftware.intern.entity.GenreEntity;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity,String> {

    boolean existsByName(String name);

    Optional<GenreEntity> findByName(String name);
}
