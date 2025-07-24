package uz.consortgroup.notification_service.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.TaskStatus;
import uz.consortgroup.core.api.v1.dto.user.request.NotificationCreateRequestDto;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTaskServiceImpl implements NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;
    private final NotificationTranslationService notificationTranslationService;
    private final NotificationRecipientService notificationRecipientService;

    @Transactional
    @Override
    public void createNotificationTask(NotificationCreateRequestDto request) {
        log.info("Creating notification task by userId={}, recipients={}, sendAt={}",
                request.getCreatedByUserId(), request.getRecipientUserIds().size(), request.getSendAt());

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
        log.info("Saved notification task: id={}", task.getId());

        notificationTranslationService.saveAll(task, request.getTranslations());
        log.info("Saved translations for taskId={}", task.getId());

        notificationRecipientService.saveAll(task, request.getRecipientUserIds());
        log.info("Saved {} recipients for taskId={}", request.getRecipientUserIds().size(), task.getId());
    }

    @Override
    public List<NotificationTask> findScheduledTasks() {
        log.info("Searching for scheduled tasks to send...");
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

            log.debug("Fetched {} tasks from page {}", pageResult.getNumberOfElements(), page);
            result.addAll(pageResult.getContent());
            page++;
        } while (!pageResult.isLast());

        log.info("Found total {} scheduled tasks to send", result.size());
        return result;
    }

    @Override
    public void markAsSent(NotificationTask task) {
        task.setStatus(TaskStatus.SENT);
        task.setUpdatedAt(LocalDateTime.now());
        notificationTaskRepository.save(task);
        log.info("Marked task as SENT: taskId={}", task.getId());
    }

    @Override
    public void markAsFailed(NotificationTask task) {
        task.setStatus(TaskStatus.FAILED);
        task.setUpdatedAt(LocalDateTime.now());
        notificationTaskRepository.save(task);
        log.warn("Marked task as FAILED: taskId={}", task.getId());
    }
}
