package viettelsoftware.intern.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.UserEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE (:userId IS NULL OR u.userId = :userId) " +
            "AND (:username IS NULL OR u.username LIKE CONCAT('%', :username, '%')) " +
            "AND (:email IS NULL OR u.email LIKE CONCAT('%', :email, '%')) " +
            "AND (:fullName IS NULL OR u.fullName LIKE CONCAT('%', :fullName, '%')) " +
            "AND (:phone IS NULL OR u.phone LIKE CONCAT('%', :phone, '%')) " +
            "AND (:address IS NULL OR u.address LIKE CONCAT('%', :address, '%'))")
    Page<UserEntity> searchUsers(
            @Param("userId") String userId,
            @Param("username") String username,
            @Param("email") String email,
            @Param("fullName") String fullName,
            @Param("phone") String phone,
            @Param("address") String address,
            Pageable pageable);

    @Query("SELECT u.username FROM UserEntity u")
    Set<String> findAllUsernames();
    @Query("SELECT u.email FROM UserEntity u")
    Set<String> findAllEmails();
}
