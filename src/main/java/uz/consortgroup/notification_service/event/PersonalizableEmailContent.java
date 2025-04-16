package uz.consortgroup.notification_service.event;

public interface PersonalizableEmailContent extends EmailContent {
    default String getFirstName() {
        return null;
    }
    default String getMiddleName() {
        return null;
    }
}
