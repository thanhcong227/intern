package viettelsoftware.intern.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    String username;
    String fullName;
    String email;
    String phone;
    String address;
    Set<RoleRequest> roles;
}
