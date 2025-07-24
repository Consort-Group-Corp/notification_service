package uz.consortgroup.notification_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;

@Component
@Slf4j
public class PasswordResetRequestValidator extends BaseEventValidator<PasswordResetRequestedEvent> {

    @Override
    protected void validateSpecificFields(List<PasswordResetRequestedEvent> events) {
        boolean hasNullUserId = events.stream().anyMatch(e -> e.getUserId() == null);
        if (hasNullUserId) {
            log.warn("Validation failed: at least one PasswordResetRequestedEvent is missing userId");
            throw new EventValidationException("Each event must contain userId");
        }
        log.debug("All PasswordResetRequestedEvent entries contain valid userId");
    }
}
