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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTaskRecipientUpdateServiceImplTest {

    @Mock
    private NotificationTaskRecipientRepository recipientRepository;

    @InjectMocks
    private NotificationTaskRecipientUpdateServiceImpl service;

    @Test
    void markAllAsSent_shouldUpdateAllRecipientsToSentStatus() {
        NotificationTask task = createTestTask();
        NotificationTaskRecipient recipient = createTestRecipient(task, NotificationStatus.PENDING);
        Page<NotificationTaskRecipient> page = new PageImpl<>(List.of(recipient));

        when(recipientRepository.findAllByTaskId(eq(task.getId()), any(PageRequest.class)))
            .thenReturn(page);

        service.markAllAsSent(task);

        verify(recipientRepository, times(1)).saveAll(any());
        assertEquals(NotificationStatus.SENT, recipient.getStatus());
        assertNotNull(recipient.getSentAt());
    }

    @Test
    void markAllAsSent_shouldProcessMultiplePages() {
        NotificationTask task = createTestTask();
        NotificationTaskRecipient recipient1 = createTestRecipient(task, NotificationStatus.PENDING);
        NotificationTaskRecipient recipient2 = createTestRecipient(task, NotificationStatus.PENDING);
        
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
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        service.markAllAsSent(task);

        verify(recipientRepository, times(2)).saveAll(any());
        assertEquals(NotificationStatus.SENT, recipient1.getStatus());
        assertEquals(NotificationStatus.SENT, recipient2.getStatus());
    }

    @Test
    void markAllAsFailed_shouldUpdateAllRecipientsToFailedStatus() {
        NotificationTask task = createTestTask();
        String errorMessage = "Test error";
        NotificationTaskRecipient recipient = createTestRecipient(task, NotificationStatus.PENDING);
        Page<NotificationTaskRecipient> page = new PageImpl<>(List.of(recipient));

        when(recipientRepository.findAllByTaskId(eq(task.getId()), any(PageRequest.class)))
            .thenReturn(page);

        service.markAllAsFailed(task, errorMessage);

        verify(recipientRepository, times(1)).saveAll(any());
        assertEquals(NotificationStatus.FAILED, recipient.getStatus());
        assertEquals(errorMessage, recipient.getErrorMessage());
        assertNotNull(recipient.getSentAt());
    }

    @Test
    void markAllAsFailed_shouldHandleNullErrorMessage() {
        NotificationTask task = createTestTask();
        NotificationTaskRecipient recipient = createTestRecipient(task, NotificationStatus.PENDING);
        Page<NotificationTaskRecipient> page = new PageImpl<>(List.of(recipient));

        when(recipientRepository.findAllByTaskId(eq(task.getId()), any(PageRequest.class)))
            .thenReturn(page);

        service.markAllAsFailed(task, null);

        verify(recipientRepository, times(1)).saveAll(any());
        assertEquals(NotificationStatus.FAILED, recipient.getStatus());
        assertNull(recipient.getErrorMessage());
    }

    private NotificationTask createTestTask() {
        NotificationTask task = new NotificationTask();
        task.setId(UUID.randomUUID());
        return task;
    }

    private NotificationTaskRecipient createTestRecipient(NotificationTask task, NotificationStatus status) {
        NotificationTaskRecipient recipient = new NotificationTaskRecipient();
        recipient.setTask(task);
        recipient.setStatus(status);
        recipient.setUserId(UUID.randomUUID());
        return recipient;
    }
}