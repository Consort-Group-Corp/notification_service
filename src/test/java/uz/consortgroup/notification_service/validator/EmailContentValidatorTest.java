package uz.consortgroup.notification_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.entity.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;
import uz.consortgroup.notification_service.service.NotificationService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailContentValidatorTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EmailContentValidator emailContentValidator;

    @Test
    void isUserProfileUpdatedEvent_returnsTrueWhenTypeIsUserProfileUpdated() {
        assertTrue(emailContentValidator.isUserProfileUpdatedEvent(EventType.USER_PROFILE_UPDATED));
    }

    @Test
    void isUserProfileUpdatedEvent_returnsFalseWhenTypeIsNotUserProfileUpdated() {
        assertFalse(emailContentValidator.isUserProfileUpdatedEvent(EventType.USER_UPDATED));
    }

    @Test
    void validateBuilder_doesNotThrowWhenBuilderIsNotNull() {
        EmailContent content = new EmailContent() {
            @Override
            public UUID getMessageId() {
                return UUID.randomUUID();
            }

            @Override
            public EventType getEventType() {
                return EventType.USER_PROFILE_UPDATED;
            }
        };

        EmailMessageBuilder builder = mock(EmailMessageBuilder.class);

        assertDoesNotThrow(() -> emailContentValidator.validateBuilder(content, builder, EventType.USER_PROFILE_UPDATED));
    }

    @Test
    void validateBuilder_throwsIllegalArgumentExceptionWhenBuilderIsNullAndContentIsNull() {
        EmailSendingException exception = assertThrows(EmailSendingException.class,
                () -> emailContentValidator.validateBuilder(null, null, EventType.USER_PROFILE_UPDATED));

        assertEquals("Email content or email address cannot be null", exception.getMessage());
    }

    @Test
    void validateBuilder_throwsIllegalArgumentExceptionWhenBuilderIsNullAndEmailIsNull() {
        EmailContent content = new EmailContent() {
            @Override
            public UUID getMessageId() {
                return UUID.randomUUID();
            }

            @Override
            public EventType getEventType() {
                return EventType.USER_PROFILE_UPDATED;
            }
        };

        EmailSendingException exception = assertThrows(EmailSendingException.class,
                () -> emailContentValidator.validateBuilder(content, null, EventType.USER_PROFILE_UPDATED));

        assertEquals("Email content or email address cannot be null", exception.getMessage());
    }

    @Test
    void validateBuilder_updatesNotificationStatusToFailedWhenBuilderIsNull() {
        EmailContent content = mock(EmailContent.class);
        when(content.getEmail()).thenReturn("test@example.com");

        assertThrows(EmailSendingException.class,
                () -> emailContentValidator.validateBuilder(content, null, EventType.USER_PROFILE_UPDATED));

        verify(notificationService).updateNotificationsStatus(List.of("test@example.com"), NotificationStatus.FAILED);
    }
}