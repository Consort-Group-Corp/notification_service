package uz.consortgroup.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.entity.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;
import uz.consortgroup.notification_service.factory.EmailBuilderFactory;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;
import uz.consortgroup.notification_service.validator.EmailContentValidator;

import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final EmailBuilderFactory builderFactory;
    private final JavaMailSender mailSender;
    private final NotificationService notificationService;
    private final EmailContentValidator emailContentValidator;

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void sendEmail(EmailContent content, Locale locale) {
        EventType type = content.getEventType();

        emailContentValidator.isUserProfileUpdatedEvent(type);

        EmailMessageBuilder builder = builderFactory.getBuilder(type);

        if (builder == null) {
            notificationService.updateNotificationsStatus(List.of(content.getEmail()), NotificationStatus.FAILED);
            throw new EmailSendingException("No builder found for event type: " + type);
        }

        emailContentValidator.validateBuilder(content, builder, type);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(content.getEmail());
            helper.setSubject(builder.buildSubject(content, locale));
            helper.setText(builder.buildBody(content, locale));

            mailSender.send(message);
            notificationService.updateNotificationsStatus(List.of(content.getEmail()), NotificationStatus.SENT);

        } catch (Exception  e) {
            notificationService.updateNotificationsStatus(List.of(content.getEmail()), NotificationStatus.FAILED);
            throw new EmailSendingException("Failed to send email");
        }
    }
}
