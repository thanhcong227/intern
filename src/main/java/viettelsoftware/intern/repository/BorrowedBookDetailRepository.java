package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.BorrowedBookDetail;

@Repository
public interface BorrowedBookDetailRepository extends JpaRepository<BorrowedBookDetail, String> {
}
