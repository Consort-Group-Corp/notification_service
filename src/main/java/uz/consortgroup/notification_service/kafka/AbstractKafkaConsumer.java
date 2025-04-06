package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.EventType;
import uz.consortgroup.notification_service.service.EmailDispatcherService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public abstract class AbstractKafkaConsumer<T extends EmailContent> {

    private final EmailDispatcherService emailDispatcherService;

    private final EventType eventType;

    protected AbstractKafkaConsumer(EmailDispatcherService emailDispatcherService,
                                    EventType eventType) {
        this.emailDispatcherService = emailDispatcherService;
        this.eventType = eventType;
    }

    protected void processBatch(List<T> messages, Acknowledgment ack) {
        messages.stream()
                .filter(Objects::nonNull)
                .forEach(message -> {
                    try {
                        emailDispatcherService.dispatch(
                                message,
                                eventType,
                                message.getLocale()
                        );
                    } catch (Exception e) {
                        log.error("Error processing message {}: {}", getMessageId(message), e.getMessage());
                    }
                });
        ack.acknowledge();
    }

    protected abstract Long getMessageId(T message);
}
