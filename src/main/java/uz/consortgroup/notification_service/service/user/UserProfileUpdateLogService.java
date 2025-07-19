package uz.consortgroup.notification_service.service.user;

import uz.consortgroup.notification_service.entity.enumeration.EventType;

import java.util.List;
import java.util.UUID;

public interface UserProfileUpdateLogService {
    void logUserProfileUpdate(List<UUID> userIds, EventType eventType);
}
