package uz.consortgroup.notification_service.firebase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.user.response.FcmTokenDto;
import uz.consortgroup.notification_service.client.UserDeviceTokenClient;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskRecipient;
import uz.consortgroup.notification_service.entity.NotificationTaskTranslation;
import uz.consortgroup.notification_service.service.notification.NotificationRecipientService;
import uz.consortgroup.notification_service.service.notification.NotificationTranslationService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirebaseNotificationServiceTest {

    @InjectMocks
    private FirebaseNotificationService firebaseNotificationService;

    @Mock
    private UserDeviceTokenClient tokenClient;

    @Mock
    private NotificationRecipientService recipientService;

    @Mock
    private NotificationTranslationService translationService;

    @Mock
    private FcmSender fcmSender;

    @Captor
    private ArgumentCaptor<String> tokenCaptor;

    private final UUID userId = UUID.randomUUID();
    private final NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendToAll_shouldSendNotificationSuccessfully() {
        NotificationTaskRecipient recipient = NotificationTaskRecipient.builder()
                .userId(userId)
                .build();

        FcmTokenDto tokenDto = new FcmTokenDto();
        tokenDto.setUserId(userId);
        tokenDto.setFcmToken("test-token");
        tokenDto.setLanguage(Language.RUSSIAN);

        NotificationTaskTranslation translation = NotificationTaskTranslation.builder()
                .title("Title")
                .message("Message")
                .build();

        when(recipientService.getRecipients(eq(task), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(recipient)))
                .thenReturn(Page.empty());

        when(tokenClient.getTokensByUserIds(List.of(userId)))
                .thenReturn(Map.of(userId, List.of(tokenDto)));

        when(translationService.getTranslation(task, Language.RUSSIAN))
                .thenReturn(translation);

        firebaseNotificationService.sendToAll(task);

        verify(fcmSender, times(1))
                .send("test-token", "Title", "Message");
    }


    @Test
    void sendToAll_shouldSkipIfNoTokens() {
        NotificationTaskRecipient recipient = NotificationTaskRecipient.builder()
                .userId(userId)
                .build();

        when(recipientService.getRecipients(eq(task), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(recipient)))
                .thenReturn(Page.empty());

        when(tokenClient.getTokensByUserIds(List.of(userId)))
                .thenReturn(Map.of(userId, List.of()));

        firebaseNotificationService.sendToAll(task);

        verify(fcmSender, never()).send(any(), any(), any());
        verify(recipientService, times(1)).markAllAsFailed(task, "No active FCM tokens found");
    }

    @Test
    void sendToAll_shouldThrowExceptionIfSendFails() {
        NotificationTaskRecipient recipient = NotificationTaskRecipient.builder()
                .userId(userId)
                .build();

        FcmTokenDto tokenDto = new FcmTokenDto();
        tokenDto.setUserId(userId);
        tokenDto.setFcmToken("bad-token");
        tokenDto.setLanguage(Language.RUSSIAN);

        NotificationTaskTranslation translation = NotificationTaskTranslation.builder()
                .title("Ошибка")
                .message("Ошибка")
                .build();

        when(recipientService.getRecipients(eq(task), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(recipient)))
                .thenReturn(Page.empty());

        when(tokenClient.getTokensByUserIds(List.of(userId)))
                .thenReturn(Map.of(userId, List.of(tokenDto)));

        when(translationService.getTranslation(task, Language.RUSSIAN))
                .thenReturn(translation);

        doThrow(new RuntimeException("FCM error"))
                .when(fcmSender).send("bad-token", "Ошибка", "Ошибка");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                firebaseNotificationService.sendToAll(task));

        assertEquals("FCM error", ex.getCause().getMessage());
    }
}
