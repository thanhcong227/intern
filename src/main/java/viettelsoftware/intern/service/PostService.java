package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.PostRequest;
import viettelsoftware.intern.dto.response.PostResponse;
import viettelsoftware.intern.entity.PostEntity;

public interface PostService {
    PostResponse create(PostRequest request);

    PostEntity update(String postId, PostEntity request);

    void delete(String postId);
    PostEntity getPost(String postId);
    Page<PostEntity> getAllPosts(Pageable pageable);
    byte[] exportPostsToExcel();
}
