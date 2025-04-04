package viettelsoftware.intern.dto.request;

import jakarta.validation.constraints.Size;
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

    @Size(min = 6, message = "129")
    String username;
    String fullName;
    @Size(min = 6, message = "130")
    String password;
    String email;
    String phone;
    String address;
    Set<RoleRequest> roles;
}
