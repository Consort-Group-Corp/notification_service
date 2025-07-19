package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.VerificationCodeProcessor;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class VerificationCodeResentKafkaConsumer extends AbstractKafkaConsumer<VerificationCodeResentEvent> {
    private final VerificationCodeProcessor processor;

    public VerificationCodeResentKafkaConsumer(EmailDispatcherService dispatcherService, VerificationCodeProcessor processor) {
        super(dispatcherService);
        this.processor = processor;
    }

    @KafkaListener(
            topics = "${kafka.verification-code-resent}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void handleVerificationCodeResentEvents(
            @Payload List<VerificationCodeResentEvent> messages,
            Acknowledgment ack
    ) {
        log.info("Received {} code resend messages: {}", messages.size(), messages);
        processor.process(messages);
        processBatch(messages, ack);
    }

    @Override
    protected UUID getMessageId(VerificationCodeResentEvent message) {
        return message.getMessageId();
    }

    @Override
    protected EventType getEventType() {
        return EventType.VERIFICATION_CODE_SENT;
    }
}