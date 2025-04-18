package viettelsoftware.intern.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import viettelsoftware.intern.dto.request.EmailObjectRequest;
import viettelsoftware.intern.entity.EmailReminder;
import viettelsoftware.intern.repository.EmailReminderRepository;
import viettelsoftware.intern.service.BorrowingService;
import viettelsoftware.intern.service.EmailReminderService;
import viettelsoftware.intern.util.EmailUtil;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailReminderServiceImpl implements EmailReminderService {

    EmailReminderRepository emailReminderRepository;
    EmailUtil emailUtil;
    BorrowingService borrowingService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Scheduled(cron = "0 0 8 * * *")
    public void generateReminderSchedule() {
        int count = borrowingService.scheduleReminderEmails();
        log.info("Đã lên lịch gửi {} email nhắc nhở", count);
    }

    @Transactional
    @Override
    @Scheduled(cron = "0 0/5 * * * *")
    public void processPendingReminders() {
        List<EmailReminder> pendingEmails = emailReminderRepository.findByScheduledTimeBeforeAndSentFalse(LocalDateTime.now());
        for (EmailReminder emailReminder : pendingEmails) {
            EmailObjectRequest emailObjectRequest = EmailObjectRequest.builder()
                    .emailTo(new String[]{emailReminder.getEmail()})
                    .subject(emailReminder.getSubject())
                    .template(emailReminder.getTemplateName())
                    .params(emailReminder.getParams())
                    .build();

            emailUtil.sendEmail(emailObjectRequest);
            emailReminder.setSent(true);
            emailReminderRepository.saveAndFlush(emailReminder);
        }
    }
}
