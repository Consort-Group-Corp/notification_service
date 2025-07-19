package uz.consortgroup.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;
import uz.consortgroup.notification_service.factory.EmailBuilderFactory;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;
import uz.consortgroup.notification_service.service.email.EmailService;
import uz.consortgroup.notification_service.service.notification.NotificationLogServiceImpl;
import uz.consortgroup.notification_service.validator.EmailContentValidator;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private EmailBuilderFactory builderFactory;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationLogServiceImpl notificationLogServiceImpl;

    @Mock
    private EmailContentValidator emailContentValidator;

    @Mock
    private EmailContent emailContent;

    @Mock
    private EmailMessageBuilder emailMessageBuilder;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_ShouldSendSuccessfully() {
        when(emailContent.getEventType()).thenReturn(EventType.USER_PROFILE_UPDATED);
        when(emailContent.getEmail()).thenReturn("test@example.com");
        when(builderFactory.getBuilder(EventType.USER_PROFILE_UPDATED)).thenReturn(emailMessageBuilder);
        when(emailMessageBuilder.buildSubject(any(), any())).thenReturn("Subject");
        when(emailMessageBuilder.buildBody(any(), any())).thenReturn("Body");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail(emailContent, Locale.ENGLISH);

        verify(mailSender).send(mimeMessage);
        verify(notificationLogServiceImpl).updateNotificationsStatus(List.of("test@example.com"), NotificationStatus.SENT);
    }

    @Test
    void sendEmail_ShouldThrowWhenMessagingException() throws MessagingException {
        when(emailContent.getEventType()).thenReturn(EventType.USER_PROFILE_UPDATED);
        when(emailContent.getEmail()).thenReturn("test@example.com");
        when(builderFactory.getBuilder(EventType.USER_PROFILE_UPDATED)).thenReturn(emailMessageBuilder);
        when(emailMessageBuilder.buildSubject(any(), any())).thenReturn("Subject");
        when(emailMessageBuilder.buildBody(any(), any())).thenReturn("Body");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        MimeMessageHelper helper = mock(MimeMessageHelper.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new RuntimeException("Failed to send email")).when(mailSender).send(mimeMessage);

        assertThrows(EmailSendingException.class, () ->
                emailService.sendEmail(emailContent, Locale.ENGLISH));

        verify(notificationLogServiceImpl).updateNotificationsStatus(List.of("test@example.com"), NotificationStatus.FAILED);
    }

    @Test
    void sendEmail_ShouldThrowWhenBuilderNotFound() {
        when(emailContent.getEventType()).thenReturn(EventType.USER_PROFILE_UPDATED);
        when(emailContent.getEmail()).thenReturn("test@example.com");
        when(builderFactory.getBuilder(EventType.USER_PROFILE_UPDATED)).thenReturn(null);

        assertThrows(EmailSendingException.class, () ->
                emailService.sendEmail(emailContent, Locale.ENGLISH));

        verify(notificationLogServiceImpl).updateNotificationsStatus(any(), eq(NotificationStatus.FAILED));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }


    @Test
    void sendEmail_ShouldValidateContent() {
        when(emailContent.getEventType()).thenReturn(EventType.USER_PROFILE_UPDATED);
        when(emailContent.getEmail()).thenReturn("test@example.com");
        when(builderFactory.getBuilder(EventType.USER_PROFILE_UPDATED)).thenReturn(emailMessageBuilder);
        when(emailMessageBuilder.buildSubject(any(), any())).thenReturn("Subject");
        when(emailMessageBuilder.buildBody(any(), any())).thenReturn("Body");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail(emailContent, Locale.ENGLISH);

        verify(emailContentValidator).isUserProfileUpdatedEvent(EventType.USER_PROFILE_UPDATED);
        verify(emailContentValidator).validateBuilder(emailContent, emailMessageBuilder, EventType.USER_PROFILE_UPDATED);
    }

}