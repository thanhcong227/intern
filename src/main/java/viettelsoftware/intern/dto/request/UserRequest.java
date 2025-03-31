package viettelsoftware.intern.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import viettelsoftware.intern.entity.RoleEntity;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {

    String username;
    String fullName;
    String password;
    String email;
    String phone;
    String address;
    Set<RoleRequest> roles;
}
