package uz.consortgroup.notification_service.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Communication;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.entity.NotificationLog;
import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.repository.NotificationLogRepository;
import uz.consortgroup.notification_service.service.user.UserInformationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationLogServiceImpl implements NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;
    private final UserInformationService userInformationService;

    @Transactional
    public void createNotification(List<UUID> userIds, EventType eventType) {
        if (userIds.isEmpty()) {
            log.info("Skipping notification creation: empty userIds list");
            return;
        }

        log.info("Creating notifications for {} users, eventType={}", userIds.size(), eventType);

        Map<UUID, UserInformation> userById = userInformationService.findAllByUserIdsInChunks(userIds).stream()
                .collect(Collectors.toMap(UserInformation::getUserId, Function.identity()));

        List<NotificationLog> notificationLogs = userIds.stream()
                .map(id -> {
                    UserInformation user = userById.get(id);
                    if (user == null) {
                        log.warn("User not found for userId={}", id);
                        return null;
                    }

                    return NotificationLog.builder()
                            .userInformation(user)
                            .eventType(eventType)
                            .notificationStatus(NotificationStatus.PENDING)
                            .communicationMethod(Communication.EMAIL)
                            .createdAt(LocalDateTime.now())
                            .sentAt(LocalDateTime.now()) // Возможно, здесь должен быть null до отправки?
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!notificationLogs.isEmpty()) {
            notificationLogRepository.saveAll(notificationLogs);
            log.info("Saved {} notification logs for eventType={}", notificationLogs.size(), eventType);
        } else {
            log.info("No valid users found to create notification logs");
        }
    }

    @Transactional
    public void updateNotificationsStatus(List<String> emails, NotificationStatus status) {
        if (emails.isEmpty()) {
            log.info("Skipping status update: empty email list");
            return;
        }

        log.info("Updating notification status to {} for {} emails", status, emails.size());

        List<UUID> userIdsByEmails = userInformationService.findUserIdsByEmails(emails);
        if (userIdsByEmails.isEmpty()) {
            log.warn("No users found for provided emails");
            return;
        }

        notificationLogRepository.updateStatusForUserIds(status, userIdsByEmails);
        log.info("Updated status for {} users to {}", userIdsByEmails.size(), status);
    }
}
