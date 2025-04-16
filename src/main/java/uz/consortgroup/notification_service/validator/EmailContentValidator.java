package uz.consortgroup.notification_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.entity.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;
import uz.consortgroup.notification_service.service.NotificationService;
import uz.consortgroup.notification_service.service.UserInformationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailContentValidator {
    private final NotificationService notificationService;

    public boolean isUserProfileUpdatedEvent(EventType type) {
        return type == EventType.USER_PROFILE_UPDATED;
    }

    public void validateBuilder(EmailContent content, EmailMessageBuilder builder, EventType type) {
        if (builder == null) {

            if (content == null || content.getEmail() == null) {
                throw new EmailSendingException("Email content or email address cannot be null");
            }
            notificationService.updateNotificationsStatus(List.of(content.getEmail()), NotificationStatus.FAILED);
            throw new EmailSendingException("Failed to send email: no builder found for event type " + type);
        }
    }

}
