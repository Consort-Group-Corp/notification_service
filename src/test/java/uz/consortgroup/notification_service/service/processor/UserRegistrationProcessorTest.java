package uz.consortgroup.notification_service.service.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.NotificationService;
import uz.consortgroup.notification_service.service.UserInformationService;
import uz.consortgroup.notification_service.validator.UserRegistrationProcessorValidator;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationProcessorTest {

    @Mock
    private EmailDispatcherService emailDispatcherService;

    @Mock
    private UserInformationService userInformationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRegistrationProcessorValidator userRegistrationProcessorValidator;

    @InjectMocks
    private UserRegistrationProcessor processor;

    @Test
    void canHandle_shouldReturnTrueOnlyForUserRegistered() {
        assertTrue(processor.canHandle(EventType.USER_REGISTERED));
        assertFalse(processor.canHandle(EventType.PASSWORD_RESET_REQUESTED));
        assertFalse(processor.canHandle(null));
    }

    @Test
    void process_shouldExecuteAllStepsForValidEvents() {
        UUID userId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID userId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        Long messageId1 = 1L;
        Long messageId2 = 2L;

        UserRegisteredEvent event1 = createTestEvent(userId1, messageId1, "user1@test.com", "CODE123", Locale.ENGLISH);
        UserRegisteredEvent event2 = createTestEvent(userId2, messageId2, "user2@test.com", "CODE456", Locale.ENGLISH);
        List<UserRegisteredEvent> events = List.of(event1, event2);

        processor.process(events);

        verify(userInformationService).saveUserBaseInfo(events);
        verify(emailDispatcherService).dispatch(
                argThat(list -> list.size() == 2 && list.containsAll(events)),
                eq(Locale.ENGLISH)
        );
        verify(notificationService).createNotification(
                argThat(list -> list.size() == 2 && list.containsAll(List.of(userId1, userId2))),
                eq(EventType.USER_REGISTERED)
        );
    }

    @Test
    void process_shouldHandleSingleEvent() {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        Long messageId = 3L;
        UserRegisteredEvent event = createTestEvent(userId, messageId, "user@test.com", "CODE789", Locale.FRENCH);

        processor.process(List.of(event));

        verify(userRegistrationProcessorValidator).validateEvents(List.of(event));
        verify(userInformationService).saveUserBaseInfo(List.of(event));
        verify(emailDispatcherService).dispatch(
                argThat(list -> list.size() == 1 && list.contains(event)),
                eq(Locale.FRENCH)
        );
        verify(notificationService).createNotification(
                argThat(list -> list.size() == 1 && list.contains(userId)),
                eq(EventType.USER_REGISTERED)
        );
    }

    @Test
    void process_shouldHandleNullEventList() {
        assertThrows(NullPointerException.class, () -> processor.process(null));
    }

    private UserRegisteredEvent createTestEvent(UUID userId, Long messageId, String email, String code, Locale locale) {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setUserId(userId);
        event.setMessageId(messageId);
        event.setEmail(email);
        event.setVerificationCode(code);
        event.setLocale(locale);
        return event;
    }
}