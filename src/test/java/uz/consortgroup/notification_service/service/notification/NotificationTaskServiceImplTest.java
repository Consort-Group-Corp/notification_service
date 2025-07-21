package uz.consortgroup.notification_service.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Communication;
import uz.consortgroup.core.api.v1.dto.user.enumeration.CreatorRole;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.user.enumeration.TaskStatus;
import uz.consortgroup.core.api.v1.dto.user.request.NotificationCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.user.request.TranslationDto;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationTaskServiceImplTest {

    private NotificationTaskRepository taskRepository;
    private NotificationTranslationService translationService;
    private NotificationRecipientService recipientService;

    private NotificationTaskServiceImpl service;

    @BeforeEach
    void setUp() {
        taskRepository = mock(NotificationTaskRepository.class);
        translationService = mock(NotificationTranslationService.class);
        recipientService = mock(NotificationRecipientService.class);
        service = new NotificationTaskServiceImpl(taskRepository, translationService, recipientService);
    }

    @Test
    void shouldCreateNotificationTask() {
        UUID creatorId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();

        TranslationDto ru = TranslationDto.builder().title("Тест").message("Сообщение").build();
        TranslationDto uz = TranslationDto.builder().title("Test").message("Xabar").build();

        NotificationCreateRequestDto request = NotificationCreateRequestDto.builder()
                .createdByUserId(creatorId)
                .creatorRole(CreatorRole.HR)
                .communication(Communication.PUSH)
                .sendAt(LocalDateTime.now().plusMinutes(5))
                .active(true)
                .recipientUserIds(List.of(recipientId))
                .translations(Map.of(Language.RUSSIAN, ru, Language.UZBEK, uz))
                .build();

        service.createNotificationTask(request);

        ArgumentCaptor<NotificationTask> taskCaptor = ArgumentCaptor.forClass(NotificationTask.class);
        verify(taskRepository).save(taskCaptor.capture());

        NotificationTask task = taskCaptor.getValue();
        assertThat(task.getCreatedByUserId()).isEqualTo(creatorId);
        assertThat(task.getCreatorRole()).isEqualTo(CreatorRole.HR);
        assertThat(task.getCommunication()).isEqualTo(Communication.PUSH);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.SCHEDULED);
        assertThat(task.getActive()).isTrue();

        verify(translationService).saveAll(eq(task), anyMap());
        verify(recipientService).saveAll(eq(task), eq(List.of(recipientId)));
    }

    @Test
    void shouldFindScheduledTasks() {
        NotificationTask task = NotificationTask.builder()
                .id(UUID.randomUUID())
                .status(TaskStatus.SCHEDULED)
                .active(true)
                .sendAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(taskRepository.findAllByActiveTrueAndStatusAndSendAtLessThanEqual(
                eq(TaskStatus.SCHEDULED), any(LocalDateTime.class), any(PageRequest.class)
        )).thenReturn(new PageImpl<>(List.of(task)));

        List<NotificationTask> result = service.findScheduledTasks();

        assertThat(result).containsExactly(task);
    }

    @Test
    void shouldMarkTaskAsSent() {
        NotificationTask task = NotificationTask.builder().build();

        service.markAsSent(task);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.SENT);
        assertThat(task.getUpdatedAt()).isNotNull();
        verify(taskRepository).save(task);
    }

    @Test
    void shouldMarkTaskAsFailed() {
        NotificationTask task = NotificationTask.builder().build();

        service.markAsFailed(task);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.FAILED);
        assertThat(task.getUpdatedAt()).isNotNull();
        verify(taskRepository).save(task);
    }
}
