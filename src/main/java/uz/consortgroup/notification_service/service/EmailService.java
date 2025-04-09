package uz.consortgroup.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterReturning;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.entity.NotificationStatus;
import uz.consortgroup.notification_service.event.EmailContent;
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
    private final UserNotificationService userNotificationService;


    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    @AspectAfterReturning
    public void sendEmail(EmailContent content, EventType type, Locale locale) {
        EmailMessageBuilder builder = builderFactory.getBuilder(type);

        if (builder == null) {
            userNotificationService.updateNotificationStatus(content.getEmail(), NotificationStatus.FAILED);
            throw new EmailSendingException("Failed to send email: no builder found for event type " + type);
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setTo(content.getEmail());
            helper.setSubject(builder.buildSubject(content, locale));
            helper.setText(builder.buildBody(content, locale));
            mailSender.send(message);
            userNotificationService.updateNotificationStatus(content.getEmail(), NotificationStatus.SENT);

        } catch (MessagingException e) {
            userNotificationService.updateNotificationStatus(content.getEmail(), NotificationStatus.FAILED);
            throw new EmailSendingException("Failed to send email");
        }
    }
}
