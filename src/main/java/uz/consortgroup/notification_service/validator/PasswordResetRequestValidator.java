package uz.consortgroup.notification_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;
import java.util.Objects;

@Component
public class PasswordResetRequestValidator extends BaseEventValidator<PasswordResetRequestedEvent> {

    @Override
    protected void validateSpecificFields(List<PasswordResetRequestedEvent> events) {
        if (events.stream().anyMatch(e -> e.getUserId() == null)) {
            throw new EventValidationException("Each event must contain userId");
        }
    }
}
