package uz.consortgroup.notification_service.service.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.service.UserInformationService;
import uz.consortgroup.notification_service.service.UserProfileUpdateLogService;
import uz.consortgroup.notification_service.validator.ProfileUpdateProcessorValidator;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileUpdateProcessorTest {

    @InjectMocks
    private ProfileUpdateProcessor processor;

    @Mock
    private UserInformationService userInformationService;

    @Mock
    private UserProfileUpdateLogService userProfileUpdateLogService;

    @Mock
    private ProfileUpdateProcessorValidator profileUpdateProcessorValidator;

    private UserProfileUpdateEvent validEvent;
    private List<UserProfileUpdateEvent> validEvents;

    @BeforeEach
    void setUp() {
        validEvent = new UserProfileUpdateEvent();
        validEvent.setUserId(UUID.randomUUID());
        validEvents = List.of(validEvent);
    }

    @Test
    void process_shouldHandleValidEvents() {
        doNothing().when(profileUpdateProcessorValidator).validateEvents(validEvents);

        processor.process(validEvents);

        verify(profileUpdateProcessorValidator, times(1)).validateEvents(validEvents);
        verify(userInformationService, times(1)).saveUserFullInfo(validEvents);
        verify(userProfileUpdateLogService, times(1)).logUserProfileUpdate(any(), eq(EventType.USER_PROFILE_UPDATED));
    }

    @Test
    void process_shouldThrowExceptionForEmptyEventList() {
        List<UserProfileUpdateEvent> emptyEvents = List.of();

        doThrow(new IllegalArgumentException("Event list must not be empty"))
                .when(profileUpdateProcessorValidator).validateEvents(emptyEvents);

        assertThrows(IllegalArgumentException.class, () -> processor.process(emptyEvents));

        verifyNoInteractions(userInformationService);
        verifyNoInteractions(userProfileUpdateLogService);
    }

    @Test
    void process_shouldThrowExceptionForInvalidEvent() {
        UserProfileUpdateEvent invalidEvent = new UserProfileUpdateEvent();
        invalidEvent.setUserId(null);
        List<UserProfileUpdateEvent> invalidEvents = List.of(invalidEvent);

        doThrow(new IllegalArgumentException("UserId must not be null"))
                .when(profileUpdateProcessorValidator).validateEvents(invalidEvents);

        assertThrows(IllegalArgumentException.class, () -> processor.process(invalidEvents));

        verifyNoInteractions(userInformationService);
        verifyNoInteractions(userProfileUpdateLogService);
    }

    @Test
    void canHandle_shouldReturnTrueForUserProfileUpdatedEvent() {
        boolean result = processor.canHandle(EventType.USER_PROFILE_UPDATED);

        assertTrue(result);
    }

    @Test
    void canHandle_shouldReturnFalseForOtherEvent() {
        boolean result = processor.canHandle(EventType.PASSWORD_RESET_REQUESTED);

        assertFalse(result);
    }
}
