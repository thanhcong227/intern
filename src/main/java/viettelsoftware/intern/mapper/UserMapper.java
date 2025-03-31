package viettelsoftware.intern.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.entity.RoleEntity;
import viettelsoftware.intern.entity.UserEntity;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toUser(UserRequest request);

    void updateUser(@MappingTarget UserEntity userEntity, UserUpdateRequest request);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
    UserResponse toUserResponse(UserEntity userEntity);

    @Named("mapRoles")
    default Set<String> mapRoles(Set<RoleEntity> roles) {
        return (roles == null || roles.isEmpty())
                ? Collections.emptySet()
                : roles.stream()
                .map(RoleEntity::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
