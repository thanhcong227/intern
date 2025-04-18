package viettelsoftware.intern.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    String subject;
    String username;
    @ElementCollection
    @CollectionTable(name = "email_reminder_books", joinColumns = @JoinColumn(name = "email_reminder_id"))
    @Column(name = "book_title")
    List<String> bookTitles;
    @Column(name = "template_name")
    String templateName;
    @ElementCollection
    @Column(name = "params", columnDefinition = "TEXT")
    private Map<String, Object> params;
    String dueDate;
    LocalDateTime scheduledTime;
    boolean sent;
}
