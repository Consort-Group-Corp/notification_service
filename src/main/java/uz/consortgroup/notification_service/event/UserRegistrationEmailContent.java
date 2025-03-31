package uz.consortgroup.notification_service.event;

public interface UserRegistrationEmailContent {
    String getEmail();
    String getFirstName();
    String getMiddleName();
    String getVerificationCode();
    default boolean isResend() { return false; }
}
