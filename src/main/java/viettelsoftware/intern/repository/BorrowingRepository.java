package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.BorrowingEntity;
import viettelsoftware.intern.entity.PermissionEntity;

@Repository
public interface BorrowingRepository extends JpaRepository<BorrowingEntity,String> {

    boolean existsByBorrowingId(String borrowingId);
}
