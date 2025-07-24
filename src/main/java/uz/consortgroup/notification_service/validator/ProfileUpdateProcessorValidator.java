package uz.consortgroup.notification_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;

@Component
@Slf4j
public class ProfileUpdateProcessorValidator extends BaseEventValidator<UserProfileUpdateEvent> {

    @Override
    protected void validateSpecificFields(List<UserProfileUpdateEvent> events) {
        log.info("Start validating UserProfileUpdateEvent list. Size: {}", events != null ? events.size() : 0);

        if (events == null || events.isEmpty()) {
            log.warn("Validation failed: event list is null or empty");
            throw new EventValidationException("Event list must not be empty");
        }

        boolean hasNullUserId = events.stream().anyMatch(e -> e.getUserId() == null);
        if (hasNullUserId) {
            log.warn("Validation failed: one or more events missing userId");
            throw new EventValidationException("Each event must contain userId");
        }

        log.info("UserProfileUpdateEvent list passed validation");
    }
}
