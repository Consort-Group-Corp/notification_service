package uz.consortgroup.notification_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;

@Component
@Slf4j
public class VerificationCodeProcessorValidator extends BaseEventValidator<VerificationCodeResentEvent> {

    @Override
    protected void validateSpecificFields(List<VerificationCodeResentEvent> events) {
        log.info("Validating VerificationCodeResentEvent list, size={}", events != null ? events.size() : 0);

        if (events == null || events.isEmpty()) {
            log.warn("Validation failed: Event list is null or empty");
            throw new EventValidationException("Event list must not be null or empty");
        }

        boolean hasNullUserId = events.stream().anyMatch(e -> e.getUserId() == null);
        if (hasNullUserId) {
            log.warn("Validation failed: One or more events have null userId");
            throw new EventValidationException("Each event must contain userId");
        }

        log.info("All VerificationCodeResentEvent entries passed validation");
    }
}
