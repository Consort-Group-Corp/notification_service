package uz.consortgroup.notification_service.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.TaskStatus;
import uz.consortgroup.core.api.v1.dto.user.request.NotificationCreateRequestDto;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationTaskServiceImpl implements NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;
    private final NotificationTranslationService notificationTranslationService;
    private final NotificationRecipientService notificationRecipientService;

    @Transactional
    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void createNotificationTask(NotificationCreateRequestDto request) {
        NotificationTask task = NotificationTask.builder()
                .createdByUserId(request.getCreatedByUserId())
                .creatorRole(request.getCreatorRole())
                .communication(request.getCommunication())
                .sendAt(request.getSendAt()
                        .atZone(ZoneId.of("Asia/Tashkent"))
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime())
                .status(TaskStatus.SCHEDULED)
                .active(request.getActive())
                .build();

        notificationTaskRepository.save(task);

        notificationTranslationService.saveAll(task, request.getTranslations());
        notificationRecipientService.saveAll(task, request.getRecipientUserIds());
    }

    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public List<NotificationTask> findScheduledTasks() {
        List<NotificationTask> result = new ArrayList<>();
        int page = 0;
        int pageSize = 200;
        Page<NotificationTask> pageResult;

        do {
            pageResult = notificationTaskRepository.findAllByActiveTrueAndStatusAndSendAtLessThanEqual(
                    TaskStatus.SCHEDULED,
                    LocalDateTime.now(),
                    PageRequest.of(page, pageSize)
            );
            result.addAll(pageResult.getContent());
            page++;
        } while (!pageResult.isLast());

        return result;
    }

    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void markAsSent(NotificationTask task) {
        task.setStatus(TaskStatus.SENT);
        task.setUpdatedAt(LocalDateTime.now());
        notificationTaskRepository.save(task);
    }

    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void markAsFailed(NotificationTask task) {
        task.setStatus(TaskStatus.FAILED);
        task.setUpdatedAt(LocalDateTime.now());
        notificationTaskRepository.save(task);
    }
}
