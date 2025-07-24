package uz.consortgroup.notification_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;

@Component
@Slf4j
public class UserRegistrationProcessorValidator extends BaseEventValidator<UserRegisteredEvent> {

    @Override
    protected void validateSpecificFields(List<UserRegisteredEvent> events) {
        log.info("Validating UserRegisteredEvent list, size={}", events != null ? events.size() : 0);

        if (events == null || events.isEmpty()) {
            log.warn("Validation failed: Event list is null or empty");
            throw new EventValidationException("Event list must not be null or empty");
        }

        boolean hasNullUserId = events.stream().anyMatch(e -> e.getUserId() == null);
        if (hasNullUserId) {
            log.warn("Validation failed: One or more events have null userId");
            throw new EventValidationException("Each event must contain userId");
        }

        log.info("All UserRegisteredEvent entries passed validation");
    }
}
