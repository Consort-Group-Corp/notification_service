package uz.consortgroup.notification_service.firebase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.user.response.FcmTokenDto;
import uz.consortgroup.notification_service.client.UserDeviceTokenClient;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskRecipient;
import uz.consortgroup.notification_service.entity.NotificationTaskTranslation;
import uz.consortgroup.notification_service.service.notification.NotificationRecipientService;
import uz.consortgroup.notification_service.service.notification.NotificationTranslationService;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseNotificationService {

    private final UserDeviceTokenClient tokenClient;
    private final NotificationRecipientService recipientService;
    private final NotificationTranslationService translationService;
    private final FcmSender fcmSender;

    private static final int PAGE_SIZE = 100;

    public void sendToAll(NotificationTask task) {
        int page = 0;

        Map<Language, NotificationTaskTranslation> translationCache = new EnumMap<>(Language.class);

        while (true) {
            Page<NotificationTaskRecipient> recipientPage = recipientService.getRecipients(task, page, PAGE_SIZE);
            List<NotificationTaskRecipient> recipients = recipientPage.getContent();
            if (recipients.isEmpty()) break;

            List<UUID> userIds = recipients.stream()
                    .map(NotificationTaskRecipient::getUserId)
                    .distinct()
                    .toList();

            Map<UUID, List<FcmTokenDto>> tokenMap = tokenClient.getTokensByUserIds(userIds);

            if (tokenMap.values().stream().allMatch(List::isEmpty)) {
                log.warn("No active tokens found for task {}", task.getId());
                recipientService.markAllAsFailed(task, "No active FCM tokens found");
                return;
            }


            for (UUID userId : userIds) {
                List<FcmTokenDto> tokens = tokenMap.getOrDefault(userId, List.of());

                for (FcmTokenDto token : tokens) {
                    try {
                        Language language = token.getLanguage() != null ? token.getLanguage() : Language.RUSSIAN;

                        NotificationTaskTranslation translation = translationCache.computeIfAbsent(language,
                                lang -> translationService.getTranslation(task, lang));

                        fcmSender.send(token.getFcmToken(), translation.getTitle(), translation.getMessage());
                    } catch (Exception e) {
                        log.error("Failed to send to token {} for user {}: {}", token.getFcmToken(), userId, e.getMessage());
                    }
                }
            }

            if (!recipientPage.hasNext()) break;
            page++;
        }
    }
}
