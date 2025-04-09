package viettelsoftware.intern.config.modelmapper;

import org.springframework.stereotype.Component;
import viettelsoftware.intern.dto.response.CommentResponse;
import viettelsoftware.intern.entity.CommentEntity;

@Component
public class CommentMapper {

    public CommentResponse toDto(CommentEntity comment) {
        CommentResponse dto = new CommentResponse();
        dto.setCommentId(comment.getCommentId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setUsername(comment.getUser().getUsername());
        return dto;
    }
}