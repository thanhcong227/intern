package viettelsoftware.intern.dto.response;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import viettelsoftware.intern.entity.CommentEntity;
import viettelsoftware.intern.entity.UserEntity;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    String postId;
    String title;
    String body;
    LocalDate createdAt;
    LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    UserEntity user;

    @OneToMany(mappedBy = "post")
    Set<CommentEntity> comments;

    @Override
    public String toString() {
        return title;
    }
}
