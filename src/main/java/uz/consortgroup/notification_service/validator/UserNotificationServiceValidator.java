package uz.consortgroup.notification_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;

import java.util.List;

@Component
public class UserNotificationServiceValidator {

    public void validateUserRegistrationEvent(UserRegistrationEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("UserRegistrationEvent must not be null");
        }

        if (event.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name must be provided");
        }
        if (event.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name must be provided");
        }
        if (event.getMiddleName().isBlank()) {
            throw new IllegalArgumentException("Middle name must be provided");
        }
    }

    public void validateUserRegistrationEvent(List<UserRegistrationEvent> events) {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("UserRegistrationEvent list must not be null or empty");
        }

        events.forEach(this::validateUserRegistrationEvent);
    }
}
