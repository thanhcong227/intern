package viettelsoftware.intern.util;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import viettelsoftware.intern.dto.request.EmailObjectRequest;

@Component
@AllArgsConstructor
@Slf4j
public class EmailUtil {

    private final TemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @SneakyThrows
    public void sendEmail(EmailObjectRequest emailRequest) {
        log.info("sendEmail:  {}", emailRequest);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(emailRequest.getEmailTo());
        if (emailRequest.getEmailCC() != null && emailRequest.getEmailCC().length > 0) {
            helper.setCc(emailRequest.getEmailCC());
        }
        if (emailRequest.getEmailBCC() != null && emailRequest.getEmailBCC().length > 0) {
            helper.setBcc(emailRequest.getEmailBCC());
        }
        helper.setSubject(emailRequest.getSubject());

        Context context = new Context();
        if (emailRequest.getParams() != null) {
            emailRequest.getParams().keySet().forEach(key -> context.setVariable(key, emailRequest.getParams().get(key)));
        }

        String html = templateEngine.process(emailRequest.getTemplate(), context);
        helper.setText(html, true);

        if (emailRequest.getFiles() != null && emailRequest.getFiles().length > 0) {
            for (int i = 0; i < emailRequest.getFiles().length; i++) {
                helper.addAttachment(emailRequest.getFileNames()[i], emailRequest.getFiles()[i]);
            }
        }
        mailSender.send(message);
    }
}