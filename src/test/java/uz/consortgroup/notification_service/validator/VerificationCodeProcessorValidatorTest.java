package uz.consortgroup.notification_service.validator;

import org.junit.jupiter.api.Test;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VerificationCodeProcessorValidatorTest {

    private final VerificationCodeProcessorValidator validator = new VerificationCodeProcessorValidator();

    @Test
    void validateSpecificFields_shouldPassWhenAllEventsHaveUserId() {
        VerificationCodeResentEvent event1 = new VerificationCodeResentEvent();
        event1.setUserId(UUID.randomUUID());

        VerificationCodeResentEvent event2 = new VerificationCodeResentEvent();
        event2.setUserId(UUID.randomUUID());

        List<VerificationCodeResentEvent> events = List.of(event1, event2);

        assertDoesNotThrow(() -> validator.validateSpecificFields(events));
    }

    @Test
    void validateSpecificFields_shouldThrowExceptionWhenUserIdIsNull() {
        VerificationCodeResentEvent event1 = new VerificationCodeResentEvent();
        event1.setUserId(null);

        VerificationCodeResentEvent event2 = new VerificationCodeResentEvent();
        event2.setUserId(UUID.randomUUID());

        List<VerificationCodeResentEvent> events = List.of(event1, event2);

        assertThrows(EventValidationException.class, () -> validator.validateSpecificFields(events));
    }
}
