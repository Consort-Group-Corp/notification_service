package uz.consortgroup.notification_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;

import java.util.List;

@Component
public class UserNotificationServiceValidator {

    public void validateUserRegistrationEvent(UserRegisteredEvent event) {
        if (event.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }

        if (event.getUserId() == null) {
            throw new IllegalArgumentException("UserId must not be null");
        }

        if (event.getLanguage() == null) {
            throw new IllegalArgumentException("Language must not be null");
        }
    }

    public void validateUserRegistrationEvent(List<UserRegisteredEvent> events) {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("UserRegistrationEvent list must not be null or empty");
        }

        events.forEach(this::validateUserRegistrationEvent);
    }
}
