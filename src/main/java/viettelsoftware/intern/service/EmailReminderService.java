package viettelsoftware.intern.service;

public interface EmailReminderService {
    void generateReminderSchedule();
    void processPendingReminders();
}
