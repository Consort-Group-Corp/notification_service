package uz.consortgroup.notification_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import uz.consortgroup.notification_service.entity.enumeration.Language;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserNotificationServiceValidatorTest {

    private final UserNotificationServiceValidator validator = new UserNotificationServiceValidator();

    @Test
    void validateUserRegistrationEvent_shouldPassWhenAllFieldsAreValid() {
        UserRegisteredEvent validEvent = createValidEvent();

        assertDoesNotThrow(() -> validator.validateUserRegistrationEvent(validEvent));
    }


    @Test
    void validateUserRegistrationEvent_shouldThrowWhenUserIdIsNull() {

        UserRegisteredEvent event = createValidEvent();
        event.setUserId(null);


        assertThrows(IllegalArgumentException.class,
                () -> validator.validateUserRegistrationEvent(event),
                "UserId must not be null");
    }

    @Test
    void validateUserRegistrationEvent_shouldThrowWhenLanguageIsNull() {

        UserRegisteredEvent event = createValidEvent();
        event.setLanguage(null);


        assertThrows(IllegalArgumentException.class,
                () -> validator.validateUserRegistrationEvent(event),
                "Language must not be null");
    }


    @Test
    void validateUserRegistrationEventList_shouldPassWhenAllEventsAreValid() {

        List<UserRegisteredEvent> events = List.of(
                createValidEvent(),
                createValidEvent(),
                createValidEvent()
        );

        assertDoesNotThrow(() -> validator.validateUserRegistrationEvent(events));
    }


    @ParameterizedTest
    @NullAndEmptySource
    void validateUserRegistrationEventList_shouldThrowWhenListIsNullOrEmpty(List<UserRegisteredEvent> events) {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validateUserRegistrationEvent(events),
                "UserRegistrationEvent list must not be null or empty");
    }

    @Test
    void validateUserRegistrationEventList_shouldThrowWhenAnyEventIsInvalid() {

        UserRegisteredEvent invalidEvent = createValidEvent();
        invalidEvent.setEmail("");

        List<UserRegisteredEvent> events = List.of(
                createValidEvent(),
                invalidEvent,
                createValidEvent()
        );


        assertThrows(IllegalArgumentException.class,
                () -> validator.validateUserRegistrationEvent(events));
    }


    private UserRegisteredEvent createValidEvent() {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEmail("user@example.com");
        event.setUserId(UUID.randomUUID());
        event.setLanguage(Language.ENGLISH);
        return event;
    }
}