package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.ProfileUpdateProcessor;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class UserProfileUpdateKafkaConsumer extends AbstractKafkaConsumer<UserProfileUpdateEvent> {
    private final ProfileUpdateProcessor processor;

    public UserProfileUpdateKafkaConsumer(EmailDispatcherService dispatcherService, ProfileUpdateProcessor processor) {
        super(dispatcherService);
        this.processor = processor;
    }

    @KafkaListener(
            topics = "${kafka.user-update-profile}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void handleUserRegistrationEvents(
            @Payload List<UserProfileUpdateEvent> messages,
            Acknowledgment ack
    ) {
        log.info("Received {} registration messages: {}", messages.size(), messages);
        processor.process(messages);
        ack.acknowledge();
    }

    @Override
    protected UUID getMessageId(UserProfileUpdateEvent message) {
        return message.getMessageId();
    }

    @Override
    protected EventType getEventType() {
        return EventType.USER_PROFILE_UPDATED;
    }
}