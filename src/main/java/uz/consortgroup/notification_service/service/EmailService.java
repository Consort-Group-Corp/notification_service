package uz.consortgroup.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.EventType;
import uz.consortgroup.notification_service.exception.EmailSendingException;
import uz.consortgroup.notification_service.factory.EmailBuilderFactory;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final EmailBuilderFactory builderFactory;
    private final JavaMailSender mailSender;

    public void sendEmail(EmailContent content, EventType type, Locale locale) {
        EmailMessageBuilder builder = builderFactory.getBuilder(type);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setTo(content.getEmail());
            helper.setSubject(builder.buildSubject(content, locale));
            helper.setText(builder.buildBody(content, locale));

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email");
        }
    }
}
