package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.CommentEntity;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity,String> {

    boolean existsByCommentId(String borrowingId);
}
