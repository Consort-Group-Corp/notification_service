package uz.consortgroup.notification_service.exception;

public class NoHandlerFoundException extends RuntimeException {
    public NoHandlerFoundException(Class<?> eventClass) {
        super("No handler found for event class: " + eventClass.getName());
    }
}