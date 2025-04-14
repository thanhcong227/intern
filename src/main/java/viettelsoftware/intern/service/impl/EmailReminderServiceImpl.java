package viettelsoftware.intern.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import viettelsoftware.intern.dto.request.EmailObjectRequest;
import viettelsoftware.intern.entity.EmailReminder;
import viettelsoftware.intern.repository.BorrowingRepository;
import viettelsoftware.intern.repository.EmailReminderRepository;
import viettelsoftware.intern.service.BorrowingService;
import viettelsoftware.intern.service.EmailReminderService;
import viettelsoftware.intern.util.EmailUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailReminderServiceImpl implements EmailReminderService {

    EmailReminderRepository emailReminderRepository;
    EmailUtil emailUtil;
    BorrowingRepository borrowingRepository;
    BorrowingService borrowingService;

    @Override
    @Scheduled(cron = "0 0 8 * * *") // Mỗi ngày lúc 8h sáng
    public void generateReminderSchedule() {
        int count = borrowingService.scheduleReminderEmails();
        log.info("Đã lên lịch gửi {} email nhắc nhở", count);
    }

    @Override
    @Scheduled(cron = "0 0 9 * * *") // Lên lịch vào lúc 9 giờ sáng mỗi ngày
    public void processPendingReminders() {
        List<EmailReminder> pendingEmails = emailReminderRepository.findByScheduledTimeBeforeAndSentFalse(LocalDateTime.now());

        for (EmailReminder emailReminder : pendingEmails) {
            EmailObjectRequest emailObjectRequest = EmailObjectRequest.builder()
                    .emailTo(new String[]{emailReminder.getEmail()})
                    .subject(emailReminder.getSubject())
                    .template("email-reminder")
                    .params(Map.of(
                            "username", emailReminder.getUsername(),
                            "dueDate", emailReminder.getDueDate(),
                            "bookTitles", emailReminder.getBookTitles()))
                    .build();

            emailUtil.sendEmail(emailObjectRequest);
            emailReminder.setSent(true);
            emailReminderRepository.save(emailReminder);
            log.info("Đã gửi email nhắc nhở đến {}", emailReminder.getEmail());
        }
    }
}
