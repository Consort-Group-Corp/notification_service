package uz.consortgroup.notification_service.service.notification;

import uz.consortgroup.core.api.v1.dto.user.request.NotificationCreateRequestDto;
import uz.consortgroup.notification_service.entity.NotificationTask;

import java.util.List;

public interface NotificationTaskService {
    void createNotificationTask(NotificationCreateRequestDto request);

    List<NotificationTask> findScheduledTasks();

    void markAsSent(NotificationTask task);

    void markAsFailed(NotificationTask task);
}
