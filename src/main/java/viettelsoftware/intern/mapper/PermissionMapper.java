package viettelsoftware.intern.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import viettelsoftware.intern.dto.request.PermissionRequest;
import viettelsoftware.intern.dto.request.RoleRequest;
import viettelsoftware.intern.dto.response.PermissionResponse;
import viettelsoftware.intern.dto.response.RoleResponse;
import viettelsoftware.intern.entity.PermissionEntity;
import viettelsoftware.intern.entity.RoleEntity;
import viettelsoftware.intern.entity.UserEntity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toPermissionResponse(PermissionEntity permissionEntity);

    @Mapping(target = "roles", ignore = true)
    PermissionEntity toPermission(PermissionRequest request);

    default Set<String> mapRoles(Set<RoleEntity> roles) {
        return roles != null ? roles.stream().map(RoleEntity::getName).collect(Collectors.toSet()) : Collections.emptySet();
    }
}
