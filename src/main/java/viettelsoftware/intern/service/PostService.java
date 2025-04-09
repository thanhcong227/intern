package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.PostRequest;
import viettelsoftware.intern.dto.response.PostResponse;
import viettelsoftware.intern.entity.PostEntity;

public interface PostService {
    PostResponse create(PostRequest request);

    PostResponse update(String postId, PostRequest request);

    void delete(String postId);
    PostResponse getPost(String postId);
    Page<PostResponse> getAllPosts(Pageable pageable);
    byte[] exportPostsToExcel();
}
