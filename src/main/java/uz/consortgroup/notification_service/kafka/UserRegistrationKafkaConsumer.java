package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;
import uz.consortgroup.notification_service.service.EmailService;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class UserRegistrationKafkaConsumer extends AbstractKafkaConsumer<UserRegistrationEvent> {

    public UserRegistrationKafkaConsumer(Set<Long> processedIds, EmailService emailService) {
        super(processedIds, emailService);
    }

    @KafkaListener(
            topics = "${kafka.user-registration}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void handleUserRegistrationEvents(@Payload List<UserRegistrationEvent> messages, Acknowledgment ack) {
        log.info("Received messages: {}", messages);
        processBatch(messages, ack, emailService::sendMail);
    }

    @Override
    protected Long getMessageId(UserRegistrationEvent message) {
        return message.getMessageId();
    }
}