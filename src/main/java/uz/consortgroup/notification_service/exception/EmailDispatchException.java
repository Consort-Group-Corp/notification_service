package uz.consortgroup.notification_service.exception;

public class EmailDispatchException extends RuntimeException {
    public EmailDispatchException(String message, Throwable th) {
        super(message, th);
    }
}
