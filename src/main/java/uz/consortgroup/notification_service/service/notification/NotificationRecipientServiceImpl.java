package uz.consortgroup.notification_service.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterReturning;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskRecipient;
import uz.consortgroup.notification_service.repository.NotificationTaskRecipientRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationRecipientServiceImpl implements NotificationRecipientService {

    private final NotificationTaskRecipientRepository recipientRepository;

    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void saveAll(NotificationTask task, List<UUID> recipientIds) {
        List<NotificationTaskRecipient> recipients = recipientIds.stream()
                .map(userId -> NotificationTaskRecipient.builder()
                        .task(task)
                        .userId(userId)
                        .status(NotificationStatus.PENDING)
                        .build())
                .toList();
        recipientRepository.saveAll(recipients);
    }

    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    @AspectAfterReturning
    public Page<NotificationTaskRecipient> getRecipients(NotificationTask task, int page, int size) {
        return recipientRepository.findAllByTaskId(task.getId(), PageRequest.of(page, size));
    }

    @Override
    @Transactional
    public void markAllAsFailed(NotificationTask task, String errorMessage) {
        int page = 0;
        int size = 500; // или сколько тебе удобно
        Page<NotificationTaskRecipient> recipientPage;

        do {
            recipientPage = recipientRepository.findAllByTaskId(task.getId(), PageRequest.of(page, size));
            for (NotificationTaskRecipient recipient : recipientPage.getContent()) {
                recipient.setStatus(NotificationStatus.FAILED);
                recipient.setErrorMessage(errorMessage);
            }
            recipientRepository.saveAll(recipientPage.getContent());
            page++;
        } while (recipientPage.hasNext());
    }
}
