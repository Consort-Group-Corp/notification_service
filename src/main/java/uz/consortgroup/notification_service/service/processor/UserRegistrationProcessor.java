package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.EventProcessor;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;
import uz.consortgroup.notification_service.service.notification.NotificationLogService;
import uz.consortgroup.notification_service.service.user.UserInformationService;
import uz.consortgroup.notification_service.validator.UserRegistrationProcessorValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRegistrationProcessor implements EventProcessor<UserRegisteredEvent> {
    private final EmailDispatcherService emailDispatcherService;
    private final UserInformationService userInformationService;
    private final NotificationLogService notificationLogService;
    private final UserRegistrationProcessorValidator userRegistrationProcessorValidator;

    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.USER_REGISTERED;
    }

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void process(List<UserRegisteredEvent> events) {
        userRegistrationProcessorValidator.validateEvents(events);
        userInformationService.saveUserBaseInfo(events);

        List<EmailContent> emailContents = events.stream()
                .map(event -> (EmailContent) event)
                .toList();
        emailDispatcherService.dispatch(emailContents, events.get(0).getLocale());

        notificationLogService.createNotification(events.stream()
                .map(UserRegisteredEvent::getUserId).toList(), EventType.USER_REGISTERED);
    }
}
