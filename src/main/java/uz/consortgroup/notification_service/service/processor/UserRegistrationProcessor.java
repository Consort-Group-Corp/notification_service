package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.EventProcessor;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;
import uz.consortgroup.notification_service.service.notification.NotificationLogService;
import uz.consortgroup.notification_service.service.user.UserInformationService;
import uz.consortgroup.notification_service.validator.UserRegistrationProcessorValidator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public void process(List<UserRegisteredEvent> events) {
        if (events == null || events.isEmpty()) {
            log.warn("No USER_REGISTERED events to process");
            return;
        }

        log.info("Processing {} USER_REGISTERED events", events.size());

        userRegistrationProcessorValidator.validateEvents(events);
        log.debug("Validated USER_REGISTERED events");

        userInformationService.saveUserBaseInfo(events);
        log.debug("Saved user base information");

        List<EmailContent> emailContents = events.stream()
                .map(event -> (EmailContent) event)
                .toList();

        emailDispatcherService.dispatch(emailContents, events.get(0).getLocale());
        log.debug("Dispatched registration emails");

        List<UUID> userIds = events.stream()
                .map(UserRegisteredEvent::getUserId)
                .toList();

        notificationLogService.createNotification(userIds, EventType.USER_REGISTERED);
        log.debug("Created notification logs for registered users");

        log.info("Finished processing USER_REGISTERED events");
    }
}
