package uz.consortgroup.notification_service.event;

public interface ResentVerificationEmailContent extends PersonalizableEmailContent {
    String getVerificationCode();
    String getEmail();
}
