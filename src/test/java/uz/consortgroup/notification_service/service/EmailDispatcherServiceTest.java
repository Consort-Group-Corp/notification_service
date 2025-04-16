package uz.consortgroup.notification_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import uz.consortgroup.notification_service.config.properties.EmailDispatchProperties;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailDispatchException;
import uz.consortgroup.notification_service.exception.EmailSendingException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailDispatcherServiceTest {

    @InjectMocks
    private EmailDispatcherService emailDispatcherService;

    @Mock
    private EmailService emailService;

    @Mock
    private ProcessedMessageTracker messageTracker;

    private final TaskExecutor async = new SimpleAsyncTaskExecutor();

    @Mock
    private EmailDispatchProperties properties;

    private EmailContent emailContent;

    @BeforeEach
    void setUp() throws Exception {
        emailContent = mock(EmailContent.class);
        when(emailContent.getMessageId()).thenReturn(1L);
        when(properties.getMaxConcurrentEmails()).thenReturn(5);
        when(properties.getChunkSize()).thenReturn(2);

        Field asyncField = EmailDispatcherService.class.getDeclaredField("async");
        asyncField.setAccessible(true);
        asyncField.set(emailDispatcherService, async);

        Semaphore semaphore = new Semaphore(properties.getMaxConcurrentEmails());
        Field semaphoreField = EmailDispatcherService.class.getDeclaredField("semaphore");
        semaphoreField.setAccessible(true);
        semaphoreField.set(emailDispatcherService, semaphore);
    }

    @Test
    void dispatch_shouldProcessEmailsSuccessfully() {
        List<EmailContent> contents = List.of(emailContent);
        when(messageTracker.isAlreadyProcessed(anyLong())).thenReturn(false);
        doNothing().when(emailService).sendEmail(any(EmailContent.class), any(Locale.class));

        assertDoesNotThrow(() -> emailDispatcherService.dispatch(contents, Locale.ENGLISH));

        verify(messageTracker, times(1)).markAsProcessed(anyLong());
        verify(emailService, times(1)).sendEmail(any(EmailContent.class), any(Locale.class));
    }

    @Test
    void dispatch_shouldRetryEmailOnFailure() throws InterruptedException {
        List<EmailContent> contents = List.of(emailContent);
        when(messageTracker.isAlreadyProcessed(anyLong())).thenReturn(false);
        doThrow(new EmailSendingException("Error"))
                .doNothing()
                .when(emailService).sendEmail(any(EmailContent.class), any(Locale.class));

        assertDoesNotThrow(() -> emailDispatcherService.dispatch(contents, Locale.ENGLISH));

        verify(emailService, times(2)).sendEmail(any(EmailContent.class), any(Locale.class));
        verify(messageTracker, times(1)).markAsProcessed(anyLong());
    }

    @Test
    void dispatch_shouldThrowExceptionWhenMaxRetriesExceeded() {
        List<EmailContent> contents = List.of(emailContent);
        when(messageTracker.isAlreadyProcessed(anyLong())).thenReturn(false);
        doThrow(new EmailSendingException("Error"))
                .when(emailService).sendEmail(any(EmailContent.class), any(Locale.class));

        assertThrows(EmailDispatchException.class,
                () -> emailDispatcherService.dispatch(contents, Locale.ENGLISH));

        verify(emailService, times(3)).sendEmail(any(EmailContent.class), any(Locale.class));
    }

    @Test
    void dispatch_shouldSkipAlreadyProcessedEmails() {
        List<EmailContent> contents = List.of(emailContent);
        when(messageTracker.isAlreadyProcessed(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> emailDispatcherService.dispatch(contents, Locale.ENGLISH));

        verify(emailService, never()).sendEmail(any(EmailContent.class), any(Locale.class));
        verify(messageTracker, never()).markAsProcessed(anyLong());
    }
}