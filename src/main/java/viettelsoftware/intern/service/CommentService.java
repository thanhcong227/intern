package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.entity.CommentEntity;
import viettelsoftware.intern.entity.PostEntity;

public interface CommentService {
    CommentEntity create(CommentEntity request);

    CommentEntity update(String commentId, CommentEntity request);

    void delete(String commentId);
    CommentEntity getComment(String commentId);
    Page<CommentEntity> getAllComments(Pageable pageable);
    byte[] exportCommentsToExcel();
}
