package uz.consortgroup.notification_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseEventValidatorTest {

    private BaseEventValidator<String> baseEventValidator;

    @BeforeEach
    void setUp() {
        baseEventValidator = new BaseEventValidator<>() {
            @Override
            protected void validateSpecificFields(List<String> events) {
                if (events.stream().anyMatch(String::isEmpty)) {
                    throw new EventValidationException("Event list contains empty values");
                }
            }
        };
    }

    @Test
    void validateEvents_ShouldNotThrow_WhenListIsValid() {
        List<String> events = List.of("Event 1", "Event 2");

        assertDoesNotThrow(() -> baseEventValidator.validateEvents(events));
    }

    @Test
    void validateEvents_ShouldThrowException_WhenListContainsNull() {
        List<String> events = new ArrayList<>();
        events.add("Event 1");
        events.add(null);

        EventValidationException exception = assertThrows(EventValidationException.class, () -> baseEventValidator.validateEvents(events));
        assertEquals("Event list must not contain null values", exception.getMessage());
    }


    @Test
    void validateEvents_ShouldNotThrow_WhenListIsEmpty() {
        List<String> events = List.of();

        assertDoesNotThrow(() -> baseEventValidator.validateEvents(events));
    }

    @Test
    void validateEvents_ShouldThrowException_WhenListContainsEmptyString() {
        List<String> events = List.of("Event 1", "");

        EventValidationException exception = assertThrows(EventValidationException.class, () -> baseEventValidator.validateEvents(events));
        assertEquals("Event list contains empty values", exception.getMessage());
    }
}
