package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.PasswordResetRequestProcessor;
import uz.consortgroup.notification_service.validator.PasswordTokenValidator;

import java.util.List;

@Slf4j
@Component
public class PasswordResetKafkaConsumer extends AbstractKafkaConsumer<PasswordResetRequestedEvent> {
    private final PasswordTokenValidator passwordTokenValidator;
    private final PasswordResetRequestProcessor process;

    protected PasswordResetKafkaConsumer(EmailDispatcherService emailDispatcherService, PasswordTokenValidator passwordTokenValidator,
                                         PasswordResetRequestProcessor processor) {
        super(emailDispatcherService);
        this.passwordTokenValidator = passwordTokenValidator;
        this.process = processor;
    }

    @KafkaListener(
            topics = "${kafka.password-reset-request}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void handleUserRegistrationEvents(@Payload List<PasswordResetRequestedEvent> messages, Acknowledgment ack) {
        log.info("Received {} password reset messages: {}", messages.size(), messages);
        passwordTokenValidator.validateTokensAsync(messages);
        passwordTokenValidator.getExpirationDatesFromTokensAsync(messages);
        process.process(messages);
        processBatch(messages, ack);
    }

    @Override
    protected Long getMessageId(PasswordResetRequestedEvent message) {
        return message.getMessageId();
    }

    @Override
    protected EventType getEventType() {
        return EventType.PASSWORD_RESET_REQUESTED;
    }
}
