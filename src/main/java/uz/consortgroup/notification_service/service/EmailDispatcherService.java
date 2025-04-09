package uz.consortgroup.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailSendingException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDispatcherService {
    private final EmailService emailService;
    private final ProcessedMessageTracker messageTracker;

    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public <T extends EmailContent> void dispatch(T content,
                                                  EventType type,
                                                  Locale locale) {
        if (messageTracker.isAlreadyProcessed(content.getMessageId())) {
            return;
        }

        try {
            emailService.sendEmail(content, type, locale);
            messageTracker.markAsProcessed(content.getMessageId());
        } catch (EmailSendingException e) {
            throw e;
        }
    }
}