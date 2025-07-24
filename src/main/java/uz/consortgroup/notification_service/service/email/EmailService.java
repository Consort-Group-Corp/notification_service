package uz.consortgroup.notification_service.service.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;
import uz.consortgroup.notification_service.factory.EmailBuilderFactory;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;
import uz.consortgroup.notification_service.service.notification.NotificationLogService;
import uz.consortgroup.notification_service.validator.EmailContentValidator;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailBuilderFactory builderFactory;
    private final JavaMailSender mailSender;
    private final NotificationLogService notificationLogService;
    private final EmailContentValidator emailContentValidator;

    @Transactional
    public void sendEmail(EmailContent content, Locale locale) {
        EventType type = content.getEventType();
        String recipient = content.getEmail();
        log.info("Attempting to send email: type={}, recipient={}, messageId={}",
                type, recipient, content.getMessageId());

        emailContentValidator.isUserProfileUpdatedEvent(type);

        EmailMessageBuilder builder = builderFactory.getBuilder(type);
        if (builder == null) {
            log.warn("No builder found for event type: {}", type);
            notificationLogService.updateNotificationsStatus(List.of(recipient), NotificationStatus.FAILED);
            throw new EmailSendingException("No builder found for event type: " + type);
        }

        emailContentValidator.validateBuilder(content, builder, type);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            String subject = builder.buildSubject(content, locale);
            String body = builder.buildBody(content, locale);

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(body);

            mailSender.send(message);
            log.info("Email successfully sent: recipient={}, subject={}", recipient, subject);
            notificationLogService.updateNotificationsStatus(List.of(recipient), NotificationStatus.SENT);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", recipient, e.getMessage(), e);
            notificationLogService.updateNotificationsStatus(List.of(recipient), NotificationStatus.FAILED);
            throw new EmailSendingException("Failed to send email");
        }
    }
}
