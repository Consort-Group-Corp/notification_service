package uz.consortgroup.notification_service.validator;

import uz.consortgroup.notification_service.exception.EventValidationException;

import java.util.List;
import java.util.Objects;

public abstract class BaseEventValidator<T> {

    public void validateEvents(List<T> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        validateNoNullElements(events);
        validateSpecificFields(events);
    }


    private void validateNoNullElements(List<T> events) {
        if (events.stream().anyMatch(Objects::isNull)) {
            throw new EventValidationException("Event list must not contain null values");
        }
    }

    protected abstract void validateSpecificFields(List<T> events);
}