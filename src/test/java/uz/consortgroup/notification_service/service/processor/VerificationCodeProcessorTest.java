package uz.consortgroup.notification_service.service.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;
import uz.consortgroup.notification_service.service.notification.NotificationLogServiceImpl;
import uz.consortgroup.notification_service.validator.VerificationCodeProcessorValidator;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class VerificationCodeProcessorTest {

    @InjectMocks
    private VerificationCodeProcessor processor;

    @Mock
    private EmailDispatcherService emailDispatcherService;

    @Mock
    private NotificationLogServiceImpl notificationLogServiceImpl;

    @Mock
    private VerificationCodeProcessorValidator verificationCodeProcessorValidator;

    private VerificationCodeResentEvent validEvent;
    private List<VerificationCodeResentEvent> validEvents;

    @BeforeEach
    void setUp() {
        validEvent = new VerificationCodeResentEvent();
        validEvent.setUserId(UUID.randomUUID());
        validEvent.setLocale(Locale.ENGLISH);
        validEvents = List.of(validEvent);
    }

    @Test
    void process_shouldHandleValidEvents() {
        doNothing().when(verificationCodeProcessorValidator).validateEvents(validEvents);

        processor.process(validEvents);

        verify(verificationCodeProcessorValidator, times(1)).validateEvents(validEvents);
        verify(emailDispatcherService, times(1)).dispatch(anyList(), eq(Locale.ENGLISH));
        verify(notificationLogServiceImpl, times(1)).createNotification(any(), eq(EventType.VERIFICATION_CODE_SENT));
    }

    @Test
    void process_shouldThrowExceptionForInvalidEvent() {
        VerificationCodeResentEvent invalidEvent = new VerificationCodeResentEvent();
        invalidEvent.setUserId(null);
        List<VerificationCodeResentEvent> invalidEvents = List.of(invalidEvent);

        doThrow(new IllegalArgumentException("UserId must not be null"))
                .when(verificationCodeProcessorValidator).validateEvents(invalidEvents);

        assertThrows(IllegalArgumentException.class, () -> processor.process(invalidEvents));

        verifyNoInteractions(emailDispatcherService);
        verifyNoInteractions(notificationLogServiceImpl);
    }

    @Test
    void canHandle_shouldReturnTrueForVerificationCodeSentEvent() {
        boolean result = processor.canHandle(EventType.VERIFICATION_CODE_SENT);

        assertTrue(result);
    }

    @Test
    void canHandle_shouldReturnFalseForOtherEvent() {
        boolean result = processor.canHandle(EventType.PASSWORD_RESET_REQUESTED);

        assertFalse(result);
    }
}
