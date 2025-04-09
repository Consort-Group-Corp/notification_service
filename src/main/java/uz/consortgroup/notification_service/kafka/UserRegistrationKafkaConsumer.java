package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.UserRegistrationProcessor;

import java.util.List;

@Slf4j
@Component
public class UserRegistrationKafkaConsumer extends AbstractKafkaConsumer<UserRegistrationEvent> {
    private final UserRegistrationProcessor userRegistrationProcessor;

    public UserRegistrationKafkaConsumer(EmailDispatcherService dispatcherService, UserRegistrationProcessor userRegistrationProcessor) {
        super(dispatcherService);
        this.userRegistrationProcessor = userRegistrationProcessor;
    }

    @KafkaListener(
            topics = "${kafka.user-registration}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void handleUserRegistrationEvents(
            @Payload List<UserRegistrationEvent> messages,
            Acknowledgment ack
    ) {
        log.info("Received {} registration messages: {}", messages.size(), messages);
        log.info("Received {} registration messages", messages.size());
        processBatch(messages, ack);
        userRegistrationProcessor.handleUserRegistrationEvents(messages);
    }

    @Override
    protected Long getMessageId(UserRegistrationEvent message) {
        return message.getMessageId();
    }

    @Override
    protected EventType getEventType() {
        return EventType.USER_REGISTERED;
    }
}