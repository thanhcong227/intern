package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.BorrowingEntity;
import viettelsoftware.intern.entity.PermissionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<BorrowingEntity,String> {

    boolean existsByBorrowingId(String borrowingId);
    List<BorrowingEntity> findByDueDate(LocalDate dueDate);
}
