package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.service.EmailService;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class VerificationCodeResentKafkaConsumer extends AbstractKafkaConsumer<VerificationCodeResentEvent> {

    public VerificationCodeResentKafkaConsumer(Set<Long> processedIds, EmailService emailService) {
        super(processedIds, emailService);
    }

    @KafkaListener(
            topics = "${kafka.verification-code-resent}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void handleVerificationCodeResentEvents(@Payload List<VerificationCodeResentEvent> messages, Acknowledgment ack) {
        processBatch(messages, ack, emailService::resendVerificationCode);
    }

    @Override
    protected Long getMessageId(VerificationCodeResentEvent message) {
        return message.getMessageId();
    }
}