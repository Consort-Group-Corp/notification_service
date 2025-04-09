package uz.consortgroup.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.entity.NotificationStatus;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;
import uz.consortgroup.notification_service.factory.EmailBuilderFactory;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {
    @Mock
    private EmailBuilderFactory builderFactory;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private EmailMessageBuilder emailMessageBuilder;

    @Mock
    private EmailContent testContent;

    @InjectMocks
    private EmailService emailService;


    private final Locale locale = Locale.ENGLISH;
    private final String testEmail = "test@example.com";
    private final EventType eventType = EventType.USER_REGISTERED;

    @BeforeEach
    void setUp() {
        when(testContent.getEmail()).thenReturn(testEmail);
    }

    @Test
    void sendEmail_WhenSuccessful_ShouldSendEmailAndUpdateStatus() {
        when(builderFactory.getBuilder(eventType)).thenReturn(emailMessageBuilder);
        when(emailMessageBuilder.buildSubject(testContent, locale)).thenReturn("Test Subject");
        when(emailMessageBuilder.buildBody(testContent, locale)).thenReturn("Test Body");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmail(testContent, eventType, locale);

        verify(mailSender).send(mimeMessage);
        verify(userNotificationService).updateNotificationStatus(testEmail, NotificationStatus.SENT);
    }

    @Test
    void sendEmail_WhenMessagingExceptionThrown_ShouldUpdateStatusToFailedAndThrowException() {
        when(builderFactory.getBuilder(eventType)).thenReturn(emailMessageBuilder);
        when(emailMessageBuilder.buildSubject(testContent, locale)).thenReturn("Test Subject");
        when(emailMessageBuilder.buildBody(testContent, locale)).thenReturn("Test Body");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doAnswer(invocation -> {
            throw new MessagingException("SMTP error");
        }).when(mailSender).send(mimeMessage);

        EmailSendingException exception = assertThrows(EmailSendingException.class, () -> {
            emailService.sendEmail(testContent, eventType, locale);
        });

        assertEquals("Failed to send email", exception.getMessage());
        verify(userNotificationService).updateNotificationStatus(testEmail, NotificationStatus.FAILED);
    }

    @Test
    void sendEmail_WhenNoBuilderFound_ShouldNotSendEmailAndUpdateStatusToFailed() {
        when(builderFactory.getBuilder(eventType)).thenReturn(null);

        EmailSendingException exception = assertThrows(EmailSendingException.class, () -> {
            emailService.sendEmail(testContent, eventType, locale);
        });

        assertEquals("Failed to send email: no builder found for event type " + eventType, exception.getMessage());
        verify(userNotificationService).updateNotificationStatus(testEmail, NotificationStatus.FAILED);
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
