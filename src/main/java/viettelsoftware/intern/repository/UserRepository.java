package viettelsoftware.intern.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.dto.request.UserSearchRequest;
import viettelsoftware.intern.entity.UserEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query(value = "SELECT u FROM UserEntity u " +
            "WHERE (:#{#request.userId} IS NULL OR LOWER(u.userId) LIKE LOWER(CONCAT('%', :#{#request.userId}, '%'))) " +
            "AND (:#{#request.username} IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :#{#request.username}, '%'))) " +
            "AND (:#{#request.email} IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :#{#request.email}, '%'))) " +
            "AND (:#{#request.fullName} IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :#{#request.fullName}, '%')))" +
            "AND (:#{#request.phone} IS NULL OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :#{#request.phone}, '%')))" +
            "AND (:#{#request.address} IS NULL OR LOWER(u.address) LIKE LOWER(CONCAT('%', :#{#request.address}, '%')))")
    Page<UserEntity> searchUsers(@Param("request") UserSearchRequest request, Pageable pageable);

    @Query("SELECT u.username FROM UserEntity u")
    Set<String> findAllUsernames();

    @Query("SELECT u.email FROM UserEntity u")
    Set<String> findAllEmails();
}
