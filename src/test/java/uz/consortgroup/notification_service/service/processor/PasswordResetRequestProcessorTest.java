package uz.consortgroup.notification_service.service.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;
import uz.consortgroup.notification_service.service.notification.NotificationLogServiceImpl;
import uz.consortgroup.notification_service.validator.PasswordResetRequestValidator;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PasswordResetRequestProcessorTest {

    @Mock
    private EmailDispatcherService emailDispatcherService;

    @Mock
    private NotificationLogServiceImpl notificationLogServiceImpl;

    @Mock
    private PasswordResetRequestValidator passwordResetRequestValidator;

    @InjectMocks
    private PasswordResetRequestProcessor processor;

    @Test
    void canHandle_shouldReturnTrueOnlyForPasswordResetEvents() {
        assertTrue(processor.canHandle(EventType.PASSWORD_RESET_REQUESTED));
        assertFalse(processor.canHandle(EventType.USER_REGISTERED));
        assertFalse(processor.canHandle(null));
    }

    @Test
    void process_shouldHandleEventsSuccessfully() {
        UUID userId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID userId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        
        PasswordResetRequestedEvent event1 = createEvent(userId1, "reset1@test.com", Locale.ENGLISH);
        PasswordResetRequestedEvent event2 = createEvent(userId2, "reset2@test.com", Locale.ENGLISH);
        List<PasswordResetRequestedEvent> events = List.of(event1, event2);

        processor.process(events);

        verify(emailDispatcherService).dispatch(
            argThat(list -> list.size() == 2 && list.containsAll(events)),
            eq(Locale.ENGLISH)
        );
        
        verify(notificationLogServiceImpl).createNotification(
            argThat(list -> list.size() == 2 && list.containsAll(List.of(userId1, userId2))),
            eq(EventType.PASSWORD_RESET_REQUESTED)
        );
    }

    @Test
    void process_shouldHandleSingleEvent() {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        PasswordResetRequestedEvent event = createEvent(userId, "reset@test.com", Locale.FRENCH);

        processor.process(List.of(event));

        verify(emailDispatcherService).dispatch(
            argThat(list -> list.size() == 1 && list.contains(event)),
            eq(Locale.FRENCH)
        );
        
        verify(notificationLogServiceImpl).createNotification(
            argThat(list -> list.size() == 1 && list.contains(userId)),
            eq(EventType.PASSWORD_RESET_REQUESTED)
        );
    }

    @Test
    void process_shouldHandleEmptyEventListSafely() {

        assertDoesNotThrow(() -> processor.process(List.of()));

        verifyNoInteractions(emailDispatcherService);
        verifyNoInteractions(notificationLogServiceImpl);
    }


    @Test
    void process_shouldUseFirstEventLocaleForDispatch() {
        UUID userId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID userId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        
        PasswordResetRequestedEvent event1 = createEvent(userId1, "reset1@test.com", Locale.FRENCH);
        PasswordResetRequestedEvent event2 = createEvent(userId2, "reset2@test.com", Locale.ENGLISH);

        processor.process(List.of(event1, event2));

        verify(emailDispatcherService).dispatch(anyList(), eq(Locale.FRENCH));
    }

    private PasswordResetRequestedEvent createEvent(UUID userId, String email, Locale locale) {
        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent();
        event.setUserId(userId);
        event.setEmail(email);
        event.setLocale(locale);
        return event;
    }
}