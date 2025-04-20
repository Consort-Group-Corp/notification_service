package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.EventProcessor;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.NotificationService;
import uz.consortgroup.notification_service.validator.PasswordResetRequestValidator;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PasswordResetRequestProcessor implements EventProcessor<PasswordResetRequestedEvent> {
    private final EmailDispatcherService emailDispatcherService;
    private final NotificationService notificationService;
    private final PasswordResetRequestValidator passwordResetRequestValidator;

    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.PASSWORD_RESET_REQUESTED;
    }

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void process(List<PasswordResetRequestedEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }

        passwordResetRequestValidator.validateEvents(events);

        List<EmailContent> emailContents = events.stream()
                .map(event -> (EmailContent) event)
                .toList();

        emailDispatcherService.dispatch(emailContents, events.get(0).getLocale());

        notificationService.createNotification(events.stream()
                .map(PasswordResetRequestedEvent::getUserId).toList(), EventType.PASSWORD_RESET_REQUESTED);
    }
}
