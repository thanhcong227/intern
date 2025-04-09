package viettelsoftware.intern.config.modelmapper;

import org.springframework.stereotype.Component;
import viettelsoftware.intern.dto.response.CommentResponse;
import viettelsoftware.intern.dto.response.PostResponse;
import viettelsoftware.intern.entity.PostEntity;

import java.util.stream.Collectors;

@Component
public class PostMapper {

    public PostResponse toDto(PostEntity post) {
        PostResponse dto = new PostResponse();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setUsername(post.getUser().getUsername());
        dto.setComments(post.getComments().stream()
                .map(comment -> new CommentResponse(comment.getCommentId(), comment.getContent(), comment.getCreatedAt(), comment.getUpdatedAt(), comment.getUser().getUsername()))
                .collect(Collectors.toSet()));
        return dto;
    }
}