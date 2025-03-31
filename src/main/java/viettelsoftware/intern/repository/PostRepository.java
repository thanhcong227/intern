package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,String> {

    boolean existsByPostId(String borrowingId);

    boolean existsByTitle(String title);
}
