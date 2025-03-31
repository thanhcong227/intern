package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.entity.BookEntity;
import viettelsoftware.intern.entity.PostEntity;

public interface PostService {
    PostEntity create(PostEntity request);

    PostEntity update(String postId, PostEntity request);

    void delete(String postId);
    PostEntity getPost(String postId);
    Page<PostEntity> getAllPosts(Pageable pageable);
    byte[] exportPostsToExcel();
}
