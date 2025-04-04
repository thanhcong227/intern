package viettelsoftware.intern.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,String> {

    boolean existsByPostId(String borrowingId);

    boolean existsByTitle(String title);

    @Query("SELECT p.title AS postTitle, COUNT(c) AS commentCount " +
            "FROM PostEntity p " +
            "JOIN p.comments c " +
            "GROUP BY p.title " +
            "ORDER BY commentCount DESC")
    Page<Object[]> getTop5MostLikedPosts(Pageable pageable);
}
