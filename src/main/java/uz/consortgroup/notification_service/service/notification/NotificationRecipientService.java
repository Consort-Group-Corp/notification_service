package uz.consortgroup.notification_service.service.notification;

import org.springframework.data.domain.Page;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskRecipient;

import java.util.List;
import java.util.UUID;

public interface NotificationRecipientService {
    void saveAll(NotificationTask task, List<UUID> recipientIds);
    Page<NotificationTaskRecipient> getRecipients(NotificationTask task, int page, int size);
    void markAllAsFailed(NotificationTask task, String errorMessage);

}
