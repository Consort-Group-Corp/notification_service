package uz.consortgroup.notification_service.event;

public interface EmailContent extends Content {
    Long getMessageId();
    String getEmail();
    String getVerificationCode();
}
