package uz.consortgroup.notification_service.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskRecipient;
import uz.consortgroup.notification_service.repository.NotificationTaskRecipientRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRecipientServiceImpl implements NotificationRecipientService {

    private final NotificationTaskRecipientRepository recipientRepository;

    @Override
    public void saveAll(NotificationTask task, List<UUID> recipientIds) {
        if (recipientIds.isEmpty()) {
            log.info("No recipients to save for taskId={}", task.getId());
            return;
        }

        List<NotificationTaskRecipient> recipients = recipientIds.stream()
                .map(userId -> NotificationTaskRecipient.builder()
                        .task(task)
                        .userId(userId)
                        .status(NotificationStatus.PENDING)
                        .build())
                .toList();

        recipientRepository.saveAll(recipients);
        log.info("Saved {} recipients for taskId={}", recipients.size(), task.getId());
    }

    @Override
    public Page<NotificationTaskRecipient> getRecipients(NotificationTask task, int page, int size) {
        log.debug("Fetching recipients: taskId={}, page={}, size={}", task.getId(), page, size);
        Page<NotificationTaskRecipient> result = recipientRepository.findAllByTaskId(task.getId(), PageRequest.of(page, size));
        log.debug("Fetched {} recipients on page {} for taskId={}", result.getNumberOfElements(), page, task.getId());
        return result;
    }

    @Override
    @Transactional
    public void markAllAsFailed(NotificationTask task, String errorMessage) {
        log.warn("Marking all recipients as FAILED for taskId={}, error={}", task.getId(), errorMessage);

        int page = 0;
        int size = 500;
        Page<NotificationTaskRecipient> recipientPage;

        do {
            recipientPage = recipientRepository.findAllByTaskId(task.getId(), PageRequest.of(page, size));
            List<NotificationTaskRecipient> recipients = recipientPage.getContent();

            for (NotificationTaskRecipient recipient : recipients) {
                recipient.setStatus(NotificationStatus.FAILED);
                recipient.setErrorMessage(errorMessage);
            }

            recipientRepository.saveAll(recipients);
            log.info("Marked {} recipients as FAILED on page {} for taskId={}", recipients.size(), page, task.getId());
            page++;
        } while (recipientPage.hasNext());

        log.info("All recipients marked as FAILED for taskId={}", task.getId());
    }
}
