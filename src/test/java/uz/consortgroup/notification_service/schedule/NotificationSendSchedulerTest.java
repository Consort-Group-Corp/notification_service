package uz.consortgroup.notification_service.schedule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.firebase.FirebaseNotificationService;
import uz.consortgroup.notification_service.service.notification.NotificationTaskRecipientUpdateService;
import uz.consortgroup.notification_service.service.notification.NotificationTaskService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSendSchedulerTest {

    private NotificationTaskService taskService;
    private NotificationTaskRecipientUpdateService recipientUpdateService;
    private FirebaseNotificationService firebaseNotificationService;

    private NotificationSendScheduler scheduler;

    @BeforeEach
    void setup() {
        taskService = mock(NotificationTaskService.class);
        recipientUpdateService = mock(NotificationTaskRecipientUpdateService.class);
        firebaseNotificationService = mock(FirebaseNotificationService.class);
        scheduler = new NotificationSendScheduler(taskService, recipientUpdateService, firebaseNotificationService);
    }

    @Test
    void shouldProcessAllScheduledTasksSuccessfully() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();

        when(taskService.findScheduledTasks()).thenReturn(List.of(task));

        scheduler.processScheduled();

        verify(firebaseNotificationService, times(1)).sendToAll(task);
        verify(recipientUpdateService, times(1)).markAllAsSent(task);
        verify(taskService, times(1)).markAsSent(task);
    }

    @Test
    void shouldHandleFailureGracefully() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();

        when(taskService.findScheduledTasks()).thenReturn(List.of(task));
        doThrow(new RuntimeException("Test Error")).when(firebaseNotificationService).sendToAll(task);

        scheduler.processScheduled();

        verify(recipientUpdateService).markAllAsFailed(eq(task), contains("Test Error"));
        verify(taskService).markAsFailed(task);
    }
}
