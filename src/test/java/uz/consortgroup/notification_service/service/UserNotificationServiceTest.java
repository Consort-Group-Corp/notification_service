package uz.consortgroup.notification_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.Communication;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.entity.Language;
import uz.consortgroup.notification_service.entity.NotificationStatus;
import uz.consortgroup.notification_service.entity.UserNotification;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;
import uz.consortgroup.notification_service.repository.UserNotificationRepository;
import uz.consortgroup.notification_service.validator.UserNotificationServiceValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private UserNotificationServiceValidator validator;

    @InjectMocks
    private UserNotificationService userNotificationService;

    private UserRegistrationEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = UserRegistrationEvent.builder()
                .userId(1L)
                .firstName("John")
                .middleName("A.")
                .lastName("Doe")
                .email("john.doe@example.com")
                .eventType(EventType.USER_REGISTERED)
                .language(Language.UZBEK)
                .communication(Communication.EMAIL)
                .build();
    }

    @Test
    void saveNewUser_ShouldSaveAndUpdateStatus() {
        userNotificationService.saveNewUser(testEvent);

        verify(validator).validateUserRegistrationEvent(testEvent);
        verify(userNotificationRepository).save(any(UserNotification.class));
        verify(userNotificationRepository).updateNotificationStatus("john.doe@example.com", NotificationStatus.SENT);
    }

    @Test
    void saveNewUserList_ShouldSaveAllAndUpdateStatuses() {
        List<UserRegistrationEvent> events = List.of(testEvent, testEvent);

        userNotificationService.saveNewUser(events);

        verify(validator).validateUserRegistrationEvent(events);
        verify(userNotificationRepository).saveAll(anyList());
        verify(userNotificationRepository).updateNotificationStatuses(
                List.of("john.doe@example.com", "john.doe@example.com"), NotificationStatus.SENT);
    }

    @Test
    void updateNotificationStatus_ShouldCallRepository() {
        userNotificationService.updateNotificationStatus("test@example.com", NotificationStatus.FAILED);
        verify(userNotificationRepository).updateNotificationStatus("test@example.com", NotificationStatus.FAILED);
    }

    @Test
    void updateNotificationStatuses_ShouldCallRepository() {
        List<String> emails = List.of("one@example.com", "two@example.com");
        userNotificationService.updateNotificationStatuses(emails, NotificationStatus.SENT);
        verify(userNotificationRepository).updateNotificationStatuses(emails, NotificationStatus.SENT);
    }

    @Test
    void saveNewUser_WhenValidationFails_ShouldNotSaveOrUpdate() {
        doThrow(new IllegalArgumentException("Invalid data"))
                .when(validator).validateUserRegistrationEvent(testEvent);

        assertThrows(IllegalArgumentException.class, () -> {
            userNotificationService.saveNewUser(testEvent);
        });

        verify(userNotificationRepository, never()).save(any());
        verify(userNotificationRepository, never()).updateNotificationStatus(any(), any());
    }

    @Test
    void saveNewUserList_WhenValidationFails_ShouldNotSaveOrUpdate() {
        List<UserRegistrationEvent> events = List.of(testEvent);
        doThrow(new IllegalArgumentException("Invalid data"))
                .when(validator).validateUserRegistrationEvent(events);

        assertThrows(IllegalArgumentException.class, () -> {
            userNotificationService.saveNewUser(events);
        });

        verify(userNotificationRepository, never()).saveAll(any());
        verify(userNotificationRepository, never()).updateNotificationStatuses(any(), any());
    }
}
