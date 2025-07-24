package uz.consortgroup.notification_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;

import java.util.List;

@Component
@Slf4j
public class UserNotificationServiceValidator {

    public void validateUserRegistrationEvent(UserRegisteredEvent event) {
        log.info("Validating single UserRegisteredEvent: userId={}, email={}", event.getUserId(), event.getEmail());

        if (event.getEmail().isBlank()) {
            log.warn("Validation failed: email is blank for userId={}", event.getUserId());
            throw new IllegalArgumentException("Email must not be blank");
        }

        if (event.getUserId() == null) {
            log.warn("Validation failed: userId is null");
            throw new IllegalArgumentException("UserId must not be null");
        }

        if (event.getLanguage() == null) {
            log.warn("Validation failed: language is null for userId={}", event.getUserId());
            throw new IllegalArgumentException("Language must not be null");
        }

        log.info("UserRegisteredEvent validation passed for userId={}", event.getUserId());
    }

    public void validateUserRegistrationEvent(List<UserRegisteredEvent> events) {
        log.info("Validating list of UserRegisteredEvent, size={}", events != null ? events.size() : 0);

        if (events == null || events.isEmpty()) {
            log.warn("Validation failed: UserRegistrationEvent list is null or empty");
            throw new IllegalArgumentException("UserRegistrationEvent list must not be null or empty");
        }

        events.forEach(this::validateUserRegistrationEvent);

        log.info("All UserRegisteredEvent entries passed validation");
    }
}
