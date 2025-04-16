package uz.consortgroup.notification_service.event;

import uz.consortgroup.notification_service.entity.enumeration.EventType;

public interface EmailContent extends Content {
    Long getMessageId();

    default String getEmail() {
        return null;
    }
    default String getVerificationCode() {
        return null;
    }

    EventType getEventType();

    default String getResetLink() {
        return "";
    }
}
