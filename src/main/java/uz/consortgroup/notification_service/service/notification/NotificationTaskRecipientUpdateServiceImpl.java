package uz.consortgroup.notification_service.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskRecipient;
import uz.consortgroup.notification_service.repository.NotificationTaskRecipientRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTaskRecipientUpdateServiceImpl implements NotificationTaskRecipientUpdateService {

    private final NotificationTaskRecipientRepository recipientRepository;

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void markAllAsSent(NotificationTask task) {
        int page = 0;
        int pageSize = 200;

        Page<NotificationTaskRecipient> pageResult;


        do {
            pageResult = recipientRepository.findAllByTaskId(task.getId(), PageRequest.of(page, pageSize));
            List<NotificationTaskRecipient> batch = pageResult.getContent();

            log.info("Marking recipients as SENT for taskId: {}", task.getId());

            batch.forEach(recipient -> {
                recipient.setStatus(NotificationStatus.SENT);
                recipient.setSentAt(LocalDateTime.now());
            });

            recipientRepository.saveAll(batch);
            log.info("Saved {} recipients for task {}", batch.size(), task.getId());
            recipientRepository.flush();
            page++;

        } while (!pageResult.isLast());
    }


    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void markAllAsFailed(NotificationTask task, String errorMessage) {
        int page = 0;
        int pageSize = 200;
        Page<NotificationTaskRecipient> pageResult;

        do {
            pageResult = recipientRepository.findAllByTaskId(task.getId(), PageRequest.of(page, pageSize));
            List<NotificationTaskRecipient> batch = pageResult.getContent();

            batch.forEach(recipient -> {
                recipient.setStatus(NotificationStatus.FAILED);
                recipient.setErrorMessage(errorMessage);
                recipient.setSentAt(LocalDateTime.now());
            });

            recipientRepository.saveAll(batch);
            page++;

        } while (!pageResult.isLast());
    }
}
