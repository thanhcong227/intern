package viettelsoftware.intern.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
public class PostRequest {

    String title;
    String body;

}
