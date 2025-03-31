package viettelsoftware.intern.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import viettelsoftware.intern.dto.request.RoleRequest;
import viettelsoftware.intern.dto.response.RoleResponse;
import viettelsoftware.intern.entity.PermissionEntity;
import viettelsoftware.intern.entity.RoleEntity;
import viettelsoftware.intern.entity.UserEntity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(source = "users", target = "users")
    RoleResponse toRoleResponse(RoleEntity roleEntity);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    RoleEntity toRole(RoleRequest request);

    default Set<String> mapUsers(Set<UserEntity> users) {
        return users != null ? users.stream().map(UserEntity::getUsername).collect(Collectors.toSet()) : Collections.emptySet();
    }

    default Set<String> mapPermissions(Set<PermissionEntity> permissions) {
        return permissions != null ? permissions.stream().map(PermissionEntity::getName).collect(Collectors.toSet()) : Collections.emptySet();
    }
}
