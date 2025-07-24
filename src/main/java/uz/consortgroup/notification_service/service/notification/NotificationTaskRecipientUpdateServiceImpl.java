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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTaskRecipientUpdateServiceImpl implements NotificationTaskRecipientUpdateService {

    private final NotificationTaskRecipientRepository recipientRepository;

    @Override
    @Transactional
    public void markAllAsSent(NotificationTask task) {
        int page = 0;
        int pageSize = 200;
        Page<NotificationTaskRecipient> pageResult;

        log.info("Starting to mark recipients as SENT for taskId={}", task.getId());

        do {
            pageResult = recipientRepository.findAllByTaskId(task.getId(), PageRequest.of(page, pageSize));
            List<NotificationTaskRecipient> batch = pageResult.getContent();

            if (batch.isEmpty()) {
                log.info("No recipients found on page {} for taskId={}", page, task.getId());
                break;
            }

            batch.forEach(recipient -> {
                recipient.setStatus(NotificationStatus.SENT);
                recipient.setSentAt(LocalDateTime.now());
            });

            recipientRepository.saveAll(batch);
            log.info("Marked {} recipients as SENT on page {} for taskId={}", batch.size(), page, task.getId());
            recipientRepository.flush();

            page++;
        } while (!pageResult.isLast());

        log.info("Finished marking all recipients as SENT for taskId={}", task.getId());
    }

    @Override
    @Transactional
    public void markAllAsFailed(NotificationTask task, String errorMessage) {
        int page = 0;
        int pageSize = 200;
        Page<NotificationTaskRecipient> pageResult;

        log.warn("Starting to mark recipients as FAILED for taskId={}, error={}", task.getId(), errorMessage);

        do {
            pageResult = recipientRepository.findAllByTaskId(task.getId(), PageRequest.of(page, pageSize));
            List<NotificationTaskRecipient> batch = pageResult.getContent();

            if (batch.isEmpty()) {
                log.info("Recipients not found on page {} for taskId={}", page, task.getId());
                break;
            }

            batch.forEach(recipient -> {
                recipient.setStatus(NotificationStatus.FAILED);
                recipient.setErrorMessage(errorMessage);
                recipient.setSentAt(LocalDateTime.now());
            });

            recipientRepository.saveAll(batch);
            log.warn("Marked {} recipients as FAILED on page {} for taskId={}", batch.size(), page, task.getId());

            page++;
        } while (!pageResult.isLast());

        log.warn("Finished marking all recipients as FAILED for taskId={}", task.getId());
    }
}
