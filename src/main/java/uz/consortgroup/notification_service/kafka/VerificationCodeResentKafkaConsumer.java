package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;

import java.util.List;

@Slf4j
@Component
public class VerificationCodeResentKafkaConsumer extends AbstractKafkaConsumer<VerificationCodeResentEvent> {

    public VerificationCodeResentKafkaConsumer(EmailDispatcherService dispatcherService) {
        super(dispatcherService);
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
        processBatch(messages, ack);
    }

    @Override
    protected Long getMessageId(VerificationCodeResentEvent message) {
        return message.getMessageId();
    }

    @Override
    protected EventType getEventType() {
        return EventType.VERIFICATION_CODE_SENT;
    }
}