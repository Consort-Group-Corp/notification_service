package uz.consortgroup.notification_service.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;
import uz.consortgroup.notification_service.factory.EmailBuilderFactory;
import uz.consortgroup.notification_service.service.notification.NotificationLogService;
import uz.consortgroup.notification_service.validator.EmailContentValidator;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailBuilderFactory builderFactory;
    private final NotificationLogService notificationLogService;
    private final EmailContentValidator emailContentValidator;
    private final MailSenderPort mailSender;

    @Value("${mail.subjectForEmail:Registration code}")
    private String defaultSubject;

    @Transactional
    public void sendEmail(EmailContent content, Locale locale) {
        var type = content.getEventType();
        var recipient = content.getEmail();

        log.info("Attempting to send email: type={}, recipient={}, messageId={}",
                type, recipient, content.getMessageId());

        emailContentValidator.isUserProfileUpdatedEvent(type);

        var builder = builderFactory.getBuilder(type);
        if (builder == null) {
            log.warn("No builder found for event type: {}", type);
            notificationLogService.updateNotificationsStatus(List.of(recipient), NotificationStatus.FAILED);
            throw new EmailSendingException("No builder found for event type: " + type);
        }

        emailContentValidator.validateBuilder(content, builder, type);

        try {
            String subjectFromBuilder = builder.buildSubject(content, locale);
            String subject = (subjectFromBuilder != null && !subjectFromBuilder.isBlank())
                    ? subjectFromBuilder
                    : defaultSubject;

            String body = builder.buildBody(content, locale);

            mailSender.send(recipient, subject, body);

            log.info("Email successfully sent: recipient={}, subject={}", recipient, subject);
            notificationLogService.updateNotificationsStatus(List.of(recipient), NotificationStatus.SENT);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", recipient, e.getMessage(), e);
            notificationLogService.updateNotificationsStatus(List.of(recipient), NotificationStatus.FAILED);
            throw new EmailSendingException("Failed to send email");
        }
    }

}

