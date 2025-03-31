package uz.consortgroup.notification_service.event;

public interface ResentVerificationEmailContent {
    String getVerificationCode();
    String getEmail();
}
