package uz.consortgroup.notification_service.service.notification;

import uz.consortgroup.notification_service.entity.NotificationTask;


public interface NotificationTaskRecipientUpdateService {
    void markAllAsSent(NotificationTask task);
    void markAllAsFailed(NotificationTask task, String errorMessage);

}
