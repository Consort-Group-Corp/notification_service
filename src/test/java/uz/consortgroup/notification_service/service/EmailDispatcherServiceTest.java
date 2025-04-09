package uz.consortgroup.notification_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailDispatcherServiceTest {
    @Mock
    private EmailService emailService;

    @Mock
    private ProcessedMessageTracker messageTracker;

    @Mock
    private EmailContent testContent;

    @InjectMocks
    private EmailDispatcherService emailDispatcherService;

    private final Locale locale = Locale.ENGLISH;
    private final long messageId = 42L;



    @BeforeEach
    void setUp() {
        when(testContent.getMessageId()).thenReturn(messageId);
    }

    @Test
    void dispatch_WhenMessageIsNotProcessed_ShouldSendEmailAndMarkAsProcessed() {
        when(messageTracker.isAlreadyProcessed(messageId)).thenReturn(false);

        emailDispatcherService.dispatch(testContent, EventType.USER_REGISTERED, locale);

        verify(emailService).sendEmail(testContent, EventType.USER_REGISTERED, locale);
        verify(messageTracker).markAsProcessed(messageId);
    }

    @Test
    void dispatch_WhenMessageIsAlreadyProcessed_ShouldDoNothing() {
        when(messageTracker.isAlreadyProcessed(messageId)).thenReturn(true);

        emailDispatcherService.dispatch(testContent, EventType.USER_REGISTERED, locale);

        verifyNoInteractions(emailService);
        verify(messageTracker, never()).markAsProcessed(anyLong());
    }

    @Test
    void dispatch_WhenSendingEmailFails_ShouldThrowException() {
        when(messageTracker.isAlreadyProcessed(messageId)).thenReturn(false);
        doThrow(new EmailSendingException("Send fail"))
                .when(emailService).sendEmail(testContent, EventType.USER_REGISTERED, locale);

        assertThrows(EmailSendingException.class, () -> {
            emailDispatcherService.dispatch(testContent, EventType.USER_REGISTERED, locale);
        });

        verify(messageTracker, never()).markAsProcessed(anyLong());
    }
}
