package uz.consortgroup.notification_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProfileUpdateProcessorValidatorTest {

    private final ProfileUpdateProcessorValidator validator = new ProfileUpdateProcessorValidator();

    @Test
    void validate_shouldPassWhenAllEventsHaveUserId() {
        UserProfileUpdateEvent event1 = new UserProfileUpdateEvent();
        event1.setUserId(UUID.randomUUID());
        
        UserProfileUpdateEvent event2 = new UserProfileUpdateEvent();
        event2.setUserId(UUID.randomUUID());
        
        List<UserProfileUpdateEvent> events = List.of(event1, event2);

        assertDoesNotThrow(() -> validator.validateEvents(events));
    }

    @Test
    void validate_shouldThrowExceptionWhenAnyEventHasNullUserId() {
        UserProfileUpdateEvent validEvent = new UserProfileUpdateEvent();
        validEvent.setUserId(UUID.randomUUID());
        
        UserProfileUpdateEvent invalidEvent = new UserProfileUpdateEvent();
        invalidEvent.setUserId(null);
        
        List<UserProfileUpdateEvent> events = List.of(validEvent, invalidEvent);

        assertThrows(EventValidationException.class, () -> validator.validateEvents(events));
    }

    @Test
    void validate_shouldThrowExceptionWhenAllEventsHaveNullUserId() {
        UserProfileUpdateEvent event1 = new UserProfileUpdateEvent();
        event1.setUserId(null);
        
        UserProfileUpdateEvent event2 = new UserProfileUpdateEvent();
        event2.setUserId(null);
        
        List<UserProfileUpdateEvent> events = List.of(event1, event2);

        assertThrows(EventValidationException.class, () -> validator.validateEvents(events));
    }
}