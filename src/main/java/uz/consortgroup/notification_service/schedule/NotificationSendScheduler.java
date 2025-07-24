package uz.consortgroup.notification_service.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.firebase.FirebaseNotificationService;
import uz.consortgroup.notification_service.service.notification.NotificationTaskRecipientUpdateService;
import uz.consortgroup.notification_service.service.notification.NotificationTaskService;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationSendScheduler {

    private final NotificationTaskService notificationTaskService;
    private final NotificationTaskRecipientUpdateService recipientUpdateService;
    private final FirebaseNotificationService firebaseNotificationService;

    @Scheduled(cron = "0/5 * * * * *")
    public void processScheduled() {
        List<NotificationTask> tasks = notificationTaskService.findScheduledTasks();

        for (NotificationTask task : tasks) {
            try {
                firebaseNotificationService.sendToAll(task);
                recipientUpdateService.markAllAsSent(task);
                notificationTaskService.markAsSent(task);
            } catch (Exception e) {
                log.error("Failed to process task {}: {}", task.getId(), e.getMessage());
                recipientUpdateService.markAllAsFailed(task, e.getMessage());
                notificationTaskService.markAsFailed(task);
            }
        }
    }
}
