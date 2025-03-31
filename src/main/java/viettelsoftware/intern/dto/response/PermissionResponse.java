package viettelsoftware.intern.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import viettelsoftware.intern.entity.PermissionEntity;
import viettelsoftware.intern.entity.RoleEntity;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionResponse {

    String permissionId;
    String name;
    Set<String> roles;
}
