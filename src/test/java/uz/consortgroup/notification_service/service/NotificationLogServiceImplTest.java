package uz.consortgroup.notification_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.repository.NotificationLogRepository;
import uz.consortgroup.notification_service.service.notification.NotificationLogServiceImpl;
import uz.consortgroup.notification_service.service.user.UserInformationServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationLogServiceImplTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private UserInformationServiceImpl userInformationServiceImpl;

    @InjectMocks
    private NotificationLogServiceImpl notificationLogServiceImpl;


    @Test
    void createNotification_ShouldCreateNotificationsForUsers() {
        UUID userId = UUID.randomUUID();
        EventType eventType = EventType.USER_PROFILE_UPDATED;

        UserInformation userInformation = new UserInformation();
        userInformation.setUserId(userId);
        userInformation.setEmail("test@example.com");

        when(userInformationServiceImpl.findAllByUserIdsInChunks(List.of(userId))).thenReturn(List.of(userInformation));

        notificationLogServiceImpl.createNotification(List.of(userId), eventType);

        verify(notificationLogRepository).saveAll(anyList());
    }

    @Test
    void createNotification_ShouldNotCreateNotificationForInvalidUser() {
        UUID userId = UUID.randomUUID();
        EventType eventType = EventType.USER_PROFILE_UPDATED;

        when(userInformationServiceImpl.findAllByUserIdsInChunks(List.of(userId))).thenReturn(List.of());

        notificationLogServiceImpl.createNotification(List.of(userId), eventType);

        verify(notificationLogRepository, never()).saveAll(anyList());
    }


    @Test
    void updateNotificationsStatus_ShouldUpdateStatusForEmails() {
        List<String> emails = List.of("test@example.com");
        NotificationStatus status = NotificationStatus.SENT;

        UUID userId = UUID.randomUUID();
        when(userInformationServiceImpl.findUserIdsByEmails(emails)).thenReturn(List.of(userId));

        notificationLogServiceImpl.updateNotificationsStatus(emails, status);

        verify(notificationLogRepository).updateStatusForUserIds(eq(status), anyList());
    }

    @Test
    void updateNotificationsStatus_ShouldUpdateStatusForValidEmails() {
        List<String> emails = List.of("valid@example.com");
        NotificationStatus status = NotificationStatus.FAILED;

        when(userInformationServiceImpl.findUserIdsByEmails(emails)).thenReturn(List.of(UUID.randomUUID()));

        notificationLogServiceImpl.updateNotificationsStatus(emails, status);

        verify(notificationLogRepository).updateStatusForUserIds(eq(status), anyList());
    }
}
