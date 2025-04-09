package uz.consortgroup.notification_service.service.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.Communication;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.entity.Language;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;
import uz.consortgroup.notification_service.service.UserNotificationService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationProcessorTest {
    @Mock
    private UserNotificationService userNotificationService;

    @InjectMocks
    private UserRegistrationProcessor userRegistrationProcessor;

    @Test
    void handleUserRegistrationEvents_Success() {
        UserRegistrationEvent userRegistrationEvent = UserRegistrationEvent.builder()
                .userId(1L)
                .language(Language.ENGLISH)
                .lastName("lastName")
                .firstName("firstName")
                .middleName("middleName")
                .bornDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("+998901234567")
                .eventType(EventType.USER_REGISTERED)
                .messageId(1L)
                .communication(Communication.EMAIL)
                .build();

        doNothing().when(userNotificationService).saveNewUser(List.of(userRegistrationEvent));

        userRegistrationProcessor.handleUserRegistrationEvents(List.of(userRegistrationEvent));

        verify(userNotificationService).saveNewUser(List.of(userRegistrationEvent));
    }

    @Test
    void handleUserRegistrationEvents_Fail() {
        UserRegistrationEvent userRegistrationEvent = UserRegistrationEvent.builder()
                .userId(1L)
                .language(Language.ENGLISH)
                .lastName(null)
                .firstName(null)
                .middleName(null)
                .bornDate(LocalDate.of(2000, 1, 1))
                .phoneNumber("+998901234567")
                .eventType(EventType.USER_REGISTERED)
                .messageId(1L)
                .communication(Communication.EMAIL)
                .build();

        doThrow(new IllegalArgumentException("First name must be provided"))
                .when(userNotificationService)
                .saveNewUser(List.of(userRegistrationEvent));

        assertThrows(IllegalArgumentException.class, () ->
                userRegistrationProcessor.handleUserRegistrationEvents(List.of(userRegistrationEvent))
        );
    }
}
