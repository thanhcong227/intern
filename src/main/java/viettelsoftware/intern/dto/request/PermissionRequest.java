package viettelsoftware.intern.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import viettelsoftware.intern.entity.RoleEntity;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {

    String name;
    Set<RoleEntity> roles;
}
