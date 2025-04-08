package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.CommentRequest;
import viettelsoftware.intern.dto.response.CommentResponse;
import viettelsoftware.intern.entity.CommentEntity;

public interface CommentService {

    CommentResponse create(CommentRequest request);

    CommentEntity update(String commentId, CommentRequest request);

    void delete(String commentId);
    CommentResponse getComment(String commentId);
    Page<CommentResponse> getAllComments(Pageable pageable);
    byte[] exportCommentsToExcel();
}
