package uz.consortgroup.notification_service.validator;

import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;

@Component
public class VerificationCodeProcessorValidator extends BaseEventValidator<VerificationCodeResentEvent> {

    @Override
    protected void validateSpecificFields(List<VerificationCodeResentEvent> events) {
        if (events.stream().anyMatch(e -> e.getUserId() == null)) {
            throw new EventValidationException("Each event must contain userId");
        }
    }
}
