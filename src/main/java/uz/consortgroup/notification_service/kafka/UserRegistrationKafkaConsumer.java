package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.UserRegistrationProcessor;

import java.util.List;
import java.util.UUID;


@Slf4j
@Component
public class UserRegistrationKafkaConsumer extends AbstractKafkaConsumer<UserRegisteredEvent> {
    private final UserRegistrationProcessor userRegistrationProcessor;

    protected UserRegistrationKafkaConsumer(EmailDispatcherService emailDispatcherService, UserRegistrationProcessor userRegistrationProcessor) {
        super(emailDispatcherService);
        this.userRegistrationProcessor = userRegistrationProcessor;
    }

    @KafkaListener(
            topics = "${kafka.user-registration}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void handleUserRegistrationEvents(
            @Payload List<UserRegisteredEvent> messages,
            Acknowledgment ack
    ) {
        log.info("Received {} registration messages: {}", messages.size(), messages);
        processBatch(messages, ack);
        userRegistrationProcessor.process(messages);
    }

    @Override
    protected UUID getMessageId(UserRegisteredEvent message) {
        return message.getMessageId();
    }

    @Override
    protected EventType getEventType() {
        return EventType.USER_REGISTERED;
    }
}
