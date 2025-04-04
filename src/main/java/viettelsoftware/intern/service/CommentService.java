package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.CommentRequest;
import viettelsoftware.intern.dto.response.CommentResponse;
import viettelsoftware.intern.entity.CommentEntity;

public interface CommentService {

    CommentResponse create(CommentRequest request);

    CommentEntity update(String commentId, CommentEntity request);

    void delete(String commentId);
    CommentEntity getComment(String commentId);
    Page<CommentEntity> getAllComments(Pageable pageable);
    byte[] exportCommentsToExcel();
}
