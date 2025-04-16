package uz.consortgroup.notification_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;

@Component
public class ProfileUpdateProcessorValidator extends BaseEventValidator<UserProfileUpdateEvent> {

    @Override
    protected void validateSpecificFields(List<UserProfileUpdateEvent> events) {
        if (events == null || events.isEmpty()) {
            throw new EventValidationException("Event list must not be empty");
        }

        if (events.stream().anyMatch(e -> e.getUserId() == null)) {
            throw new EventValidationException("Each event must contain userId");
        }
    }
}