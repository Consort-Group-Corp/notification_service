package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.EventProcessor;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;
import uz.consortgroup.notification_service.service.notification.NotificationLogService;
import uz.consortgroup.notification_service.validator.VerificationCodeProcessorValidator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeProcessor implements EventProcessor<VerificationCodeResentEvent> {

    private final EmailDispatcherService emailDispatcherService;
    private final NotificationLogService notificationLogService;
    private final VerificationCodeProcessorValidator verificationCodeProcessorValidator;

    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.VERIFICATION_CODE_SENT;
    }

    @Override
    @Transactional
    public void process(List<VerificationCodeResentEvent> events) {
        if (events == null || events.isEmpty()) {
            log.warn("No VERIFICATION_CODE_SENT events to process");
            return;
        }

        log.info("Processing {} VERIFICATION_CODE_SENT events", events.size());

        verificationCodeProcessorValidator.validateEvents(events);
        log.debug("Validated VERIFICATION_CODE_SENT events");

        List<EmailContent> emailContents = events.stream()
                .map(event -> (EmailContent) event)
                .toList();

        emailDispatcherService.dispatch(emailContents, events.get(0).getLocale());
        log.debug("Dispatched verification code emails");

        List<UUID> userIds = events.stream()
                .map(VerificationCodeResentEvent::getUserId)
                .toList();

        notificationLogService.createNotification(userIds, EventType.VERIFICATION_CODE_SENT);
        log.debug("Created notification logs for verification code events");

        log.info("Finished processing VERIFICATION_CODE_SENT events");
    }
}
