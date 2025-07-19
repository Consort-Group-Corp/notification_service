package uz.consortgroup.notification_service.service.notification;

import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.entity.enumeration.EventType;

import java.util.List;
import java.util.UUID;

public interface NotificationLogService {
    void createNotification(List<UUID> userIds, EventType eventType);
    void updateNotificationsStatus(List<String> emails, NotificationStatus status);
}
