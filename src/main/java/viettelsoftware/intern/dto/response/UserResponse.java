package viettelsoftware.intern.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String userId;
    String username;
    String fullName;
    String email;
    String phone;
    String address;
    Set<String> roles;
}
