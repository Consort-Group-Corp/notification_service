package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.EventProcessor;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.NotificationService;
import uz.consortgroup.notification_service.validator.VerificationCodeProcessorValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationCodeProcessor implements EventProcessor<VerificationCodeResentEvent> {
    private final EmailDispatcherService emailDispatcherService;
    private final NotificationService notificationService;
    private final VerificationCodeProcessorValidator verificationCodeProcessorValidator;

    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.VERIFICATION_CODE_SENT;
    }

    @Override
    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void process(List<VerificationCodeResentEvent> events) {
        verificationCodeProcessorValidator.validateEvents(events);

        List<EmailContent> emailContents = events.stream()
                .map(event -> (EmailContent) event)
                .toList();

        emailDispatcherService.dispatch(emailContents, events.get(0).getLocale());

        notificationService.createNotification(events.stream()
                .map(VerificationCodeResentEvent::getUserId).toList(), EventType.VERIFICATION_CODE_SENT);
    }
}
