package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.EventProcessor;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;
import uz.consortgroup.notification_service.service.notification.NotificationLogService;
import uz.consortgroup.notification_service.validator.PasswordResetRequestValidator;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PasswordResetRequestProcessor implements EventProcessor<PasswordResetRequestedEvent> {

    private final EmailDispatcherService emailDispatcherService;
    private final NotificationLogService notificationLogService;
    private final PasswordResetRequestValidator passwordResetRequestValidator;

    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.PASSWORD_RESET_REQUESTED;
    }

    @Override
    @Transactional
    public void process(List<PasswordResetRequestedEvent> events) {
        if (events == null || events.isEmpty()) {
            log.warn("No events to process for PASSWORD_RESET_REQUESTED");
            return;
        }

        log.info("Processing {} password reset events", events.size());

        passwordResetRequestValidator.validateEvents(events);

        List<EmailContent> emailContents = events.stream()
                .map(event -> (EmailContent) event)
                .toList();

        log.debug("Dispatching emails for {} users", emailContents.size());
        emailDispatcherService.dispatch(emailContents, events.get(0).getLocale());

        List<java.util.UUID> userIds = events.stream()
                .map(PasswordResetRequestedEvent::getUserId)
                .toList();

        notificationLogService.createNotification(userIds, EventType.PASSWORD_RESET_REQUESTED);
        log.info("Notification logs created for {} users", userIds.size());
    }
}
