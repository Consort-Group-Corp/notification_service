package uz.consortgroup.notification_service.event;

public interface PersonalizableEmailContent extends EmailContent {
    String getFirstName();
    String getMiddleName();
}
