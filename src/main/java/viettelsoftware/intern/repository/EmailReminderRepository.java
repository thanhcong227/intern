package viettelsoftware.intern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import viettelsoftware.intern.entity.EmailReminder;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailReminderRepository extends JpaRepository<EmailReminder, String> {

    List<EmailReminder> findByScheduledTimeBeforeAndSentFalse(LocalDateTime scheduledTime);
}
