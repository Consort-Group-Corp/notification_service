package uz.consortgroup.notification_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import uz.consortgroup.notification_service.config.properties.EmailDispatchProperties;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailDispatchException;
import uz.consortgroup.notification_service.exception.EmailSendingException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailDispatcherServiceTest {

    @InjectMocks
    private EmailDispatcherService emailDispatcherService;

    @Mock
    private EmailService emailService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private EmailDispatchProperties properties;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private TaskExecutor async = new SimpleAsyncTaskExecutor();
    private EmailContent emailContent;
    private UUID messageId;

    @BeforeEach
    void setUp() throws Exception {
        when(properties.getMaxConcurrentEmails()).thenReturn(5);
        when(properties.getChunkSize()).thenReturn(10);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        messageId = UUID.randomUUID();
        emailContent = mock(EmailContent.class);
        when(emailContent.getMessageId()).thenReturn(messageId);

        Field asyncField = EmailDispatcherService.class.getDeclaredField("async");
        asyncField.setAccessible(true);
        asyncField.set(emailDispatcherService, async);

        Method init = EmailDispatcherService.class.getDeclaredMethod("initSemaphore");
        init.setAccessible(true);
        init.invoke(emailDispatcherService);
    }

    @Test
    void dispatch_shouldProcessEmailsSuccessfully() {
        when(valueOperations.setIfAbsent(anyString(), eq("true"), any(Duration.class))).thenReturn(true);
        doNothing().when(emailService).sendEmail(emailContent, Locale.ENGLISH);

        List<EmailContent> contents = List.of(emailContent);

        assertDoesNotThrow(() -> emailDispatcherService.dispatch(contents, Locale.ENGLISH));

        verify(valueOperations).setIfAbsent(contains(messageId.toString()), eq("true"), any(Duration.class));
        verify(emailService, times(1)).sendEmail(emailContent, Locale.ENGLISH);
    }

    @Test
    void dispatch_shouldSkipAlreadyProcessedEmails() {
        when(valueOperations.setIfAbsent(anyString(), eq("true"), any(Duration.class))).thenReturn(false);
        List<EmailContent> contents = List.of(emailContent);

        assertDoesNotThrow(() -> emailDispatcherService.dispatch(contents, Locale.ENGLISH));

        verify(emailService, never()).sendEmail(any(), any());
    }

    @Test
    void dispatch_shouldRetryEmailOnFailure() {
        when(valueOperations.setIfAbsent(anyString(), eq("true"), any(Duration.class))).thenReturn(true);
        doThrow(new EmailSendingException("Fail"))
                .doNothing()
                .when(emailService).sendEmail(emailContent, Locale.ENGLISH);

        List<EmailContent> contents = List.of(emailContent);

        assertDoesNotThrow(() -> emailDispatcherService.dispatch(contents, Locale.ENGLISH));

        verify(emailService, times(2)).sendEmail(emailContent, Locale.ENGLISH);
    }

    @Test
    void dispatch_shouldThrowExceptionWhenMaxRetriesExceeded() {
        when(valueOperations.setIfAbsent(anyString(), eq("true"), any(Duration.class))).thenReturn(true);
        doThrow(new EmailSendingException("Fatal"))
                .when(emailService).sendEmail(emailContent, Locale.ENGLISH);

        List<EmailContent> contents = List.of(emailContent);

        EmailDispatchException ex = assertThrows(EmailDispatchException.class, () ->
                emailDispatcherService.dispatch(contents, Locale.ENGLISH)
        );
        assertTrue(ex.getMessage().contains("Error sending email"));

        verify(emailService, times(3)).sendEmail(emailContent, Locale.ENGLISH);
    }
}
