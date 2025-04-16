package uz.consortgroup.notification_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.entity.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.repository.NotificationRepository;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserInformationService userInformationService;

    @InjectMocks
    private NotificationService notificationService;


    @Test
    void createNotification_ShouldCreateNotificationsForUsers() {
        UUID userId = UUID.randomUUID();
        EventType eventType = EventType.USER_PROFILE_UPDATED;

        UserInformation userInformation = new UserInformation();
        userInformation.setUserId(userId);
        userInformation.setEmail("test@example.com");

        when(userInformationService.findAllByUserIds(List.of(userId))).thenReturn(List.of(userInformation));

        notificationService.createNotification(List.of(userId), eventType);

        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    void createNotification_ShouldNotCreateNotificationForInvalidUser() {
        UUID userId = UUID.randomUUID();
        EventType eventType = EventType.USER_PROFILE_UPDATED;

        when(userInformationService.findAllByUserIds(List.of(userId))).thenReturn(List.of());

        notificationService.createNotification(List.of(userId), eventType);

        verify(notificationRepository, never()).saveAll(anyList());
    }


    @Test
    void updateNotificationsStatus_ShouldUpdateStatusForEmails() {
        List<String> emails = List.of("test@example.com");
        NotificationStatus status = NotificationStatus.SENT;

        UUID userId = UUID.randomUUID();
        when(userInformationService.findUserIdsByEmails(emails)).thenReturn(List.of(userId));

        notificationService.updateNotificationsStatus(emails, status);

        verify(notificationRepository).updateStatusForUserIds(eq(status), anyList());
    }

    @Test
    void updateNotificationsStatus_ShouldUpdateStatusForValidEmails() {
        List<String> emails = List.of("valid@example.com");
        NotificationStatus status = NotificationStatus.FAILED;

        when(userInformationService.findUserIdsByEmails(emails)).thenReturn(List.of(UUID.randomUUID()));

        notificationService.updateNotificationsStatus(emails, status);

        verify(notificationRepository).updateStatusForUserIds(eq(status), anyList());
    }
}
