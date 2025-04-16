package uz.consortgroup.notification_service.validator;

import org.junit.jupiter.api.Test;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordResetRequestValidatorTest {

    private final PasswordResetRequestValidator validator = new PasswordResetRequestValidator();

    @Test
    void validateSpecificFields_shouldPassWhenAllEventsHaveUserId() {
        PasswordResetRequestedEvent passwordResetRequestedEvent = PasswordResetRequestedEvent
                .builder()
                .userId(UUID.randomUUID())
                .build();

        PasswordResetRequestedEvent passwordResetRequestedEvent2 = PasswordResetRequestedEvent
                .builder()
                .userId(UUID.randomUUID())
                .build();

        List<PasswordResetRequestedEvent> events = List.of(
                passwordResetRequestedEvent,
                passwordResetRequestedEvent2
        );

        assertDoesNotThrow(() -> validator.validateSpecificFields(events));
    }


    @Test
    void validateSpecificFields_shouldThrowExceptionWhenAnyEventHasNullUserId() {
        List<PasswordResetRequestedEvent> events = List.of(
                new PasswordResetRequestedEvent(),
                new PasswordResetRequestedEvent()
        );
        assertThrows(EventValidationException.class, () -> validator.validateSpecificFields(events));
    }

    @Test
    void validateSpecificFields_shouldThrowExceptionWhenAllEventsHaveNullUserId() {
        List<PasswordResetRequestedEvent> events = List.of(
                new PasswordResetRequestedEvent(),
                new PasswordResetRequestedEvent()
        );
        assertThrows(EventValidationException.class, () -> validator.validateSpecificFields(events));
    }

    @Test
    void validateSpecificFields_shouldPassForEmptyList() {
        List<PasswordResetRequestedEvent> events = List.of();
        assertDoesNotThrow(() -> validator.validateSpecificFields(events));
    }
}