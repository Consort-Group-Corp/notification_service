package uz.consortgroup.notification_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;

@Component
public class UserRegistrationProcessorValidator extends BaseEventValidator<UserRegisteredEvent> {

    @Override
    protected void validateSpecificFields(List<UserRegisteredEvent> events) {

        if (events.stream().anyMatch(e -> e.getUserId() == null)) {
            throw new EventValidationException("Each event must contain userId");
        }
    }
}
