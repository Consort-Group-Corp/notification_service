package uz.consortgroup.notification_service.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskRecipient;
import uz.consortgroup.notification_service.repository.NotificationTaskRecipientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationRecipientServiceImplTest {

    @Mock
    private NotificationTaskRecipientRepository recipientRepository;

    @InjectMocks
    private NotificationRecipientServiceImpl notificationRecipientService;

    @Test
    void saveAll_shouldSaveAllRecipients() {
        NotificationTask task = new NotificationTask();
        List<UUID> recipientIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        
        notificationRecipientService.saveAll(task, recipientIds);
        
        verify(recipientRepository, times(1)).saveAll(any());
    }

    @Test
    void saveAll_shouldCreateRecipientsWithPendingStatus() {
        NotificationTask task = new NotificationTask();
        List<UUID> recipientIds = List.of(UUID.randomUUID());
        
        notificationRecipientService.saveAll(task, recipientIds);
        
        verify(recipientRepository, times(1)).saveAll(any());
    }

    @Test
    void getRecipients_shouldReturnPagedResults() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<NotificationTaskRecipient> expectedPage = new PageImpl<>(List.of(
            new NotificationTaskRecipient(),
            new NotificationTaskRecipient()
        ));
        
        when(recipientRepository.findAllByTaskId(task.getId(), pageable))
            .thenReturn(expectedPage);
        
        Page<NotificationTaskRecipient> result = notificationRecipientService.getRecipients(task, 0, 10);
        
        assertEquals(expectedPage, result);
        verify(recipientRepository).findAllByTaskId(task.getId(), pageable);
    }

    @Test
    void getRecipients_shouldHandleEmptyResults() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<NotificationTaskRecipient> emptyPage = Page.empty();
        
        when(recipientRepository.findAllByTaskId(task.getId(), pageable))
            .thenReturn(emptyPage);
        
        Page<NotificationTaskRecipient> result = notificationRecipientService.getRecipients(task, 0, 10);
        
        assertTrue(result.isEmpty());
    }

    @Test
    void markAllAsFailed_shouldUpdateAllRecipients() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();
        String errorMessage = "Test error";
        NotificationTaskRecipient recipient1 = NotificationTaskRecipient.builder()
            .task(task)
            .status(NotificationStatus.PENDING)
            .build();
        NotificationTaskRecipient recipient2 = NotificationTaskRecipient.builder()
            .task(task)
            .status(NotificationStatus.PENDING)
            .build();
        
        Page<NotificationTaskRecipient> page = new PageImpl<>(List.of(recipient1, recipient2));
        
        when(recipientRepository.findAllByTaskId(eq(task.getId()), any(PageRequest.class)))
            .thenReturn(page);
        
        notificationRecipientService.markAllAsFailed(task, errorMessage);
        
       verify(recipientRepository, times(1)).saveAll(any());
    }


    @Test
    void markAllAsFailed_shouldProcessPaginationCorrectly() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();
        String errorMessage = "Test error";
        
        NotificationTaskRecipient recipient1 = NotificationTaskRecipient.builder().task(task).build();
        NotificationTaskRecipient recipient2 = NotificationTaskRecipient.builder().task(task).build();
        
        Page<NotificationTaskRecipient> firstPage = new PageImpl<>(
            List.of(recipient1), 
            PageRequest.of(0, 1), 
            2
        );
        Page<NotificationTaskRecipient> secondPage = new PageImpl<>(
            List.of(recipient2), 
            PageRequest.of(1, 1), 
            2
        );
        
        when(recipientRepository.findAllByTaskId(eq(task.getId()), any(PageRequest.class)))
            .thenReturn(firstPage)
            .thenReturn(secondPage)
            .thenReturn(Page.empty());
        
        notificationRecipientService.markAllAsFailed(task, errorMessage);
        
        verify(recipientRepository, times(2)).saveAll(any());
    }
}