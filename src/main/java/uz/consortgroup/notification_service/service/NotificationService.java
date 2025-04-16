package uz.consortgroup.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.Notification;
import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.entity.enumeration.Communication;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.entity.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserInformationService userInformationService;

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void createNotification(List<UUID> userIds, EventType eventType) {
        if (userIds.isEmpty()) {
            return;
        }

        Map<UUID, UserInformation> userById = userInformationService.findAllByUserIds(userIds).stream()
                .collect(Collectors.toMap(UserInformation::getUserId, Function.identity()));


        List<Notification> notifications = userIds.stream()
                .map(id -> {
                    UserInformation user = userById.get(id);
                    if (user == null) {
                        return null;
                    }

                    return Notification.builder()
                            .userInformation(user)
                            .eventType(eventType)
                            .notificationStatus(NotificationStatus.PENDING)
                            .communicationMethod(Communication.EMAIL)
                            .createdAt(LocalDateTime.now())
                            .sentAt(LocalDateTime.now())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void updateNotificationsStatus(List<String> emails, NotificationStatus status) {
        List<UUID> userIdsByEmails = userInformationService.findUserIdsByEmails(emails);
        notificationRepository.updateStatusForUserIds(status, userIdsByEmails);
    }
}
