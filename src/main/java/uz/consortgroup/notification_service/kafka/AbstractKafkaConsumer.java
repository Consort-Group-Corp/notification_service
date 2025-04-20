package uz.consortgroup.notification_service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.service.EmailDispatcherService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public abstract class AbstractKafkaConsumer<T extends EmailContent> {
    private final EmailDispatcherService emailDispatcherService;

    protected AbstractKafkaConsumer(EmailDispatcherService emailDispatcherService) {
        this.emailDispatcherService = emailDispatcherService;
    }

    protected void processBatch(List<T> messages, Acknowledgment ack) {
        List<CompletableFuture<Void>> futures = messages.stream()
                .filter(Objects::nonNull)
                .map(message -> CompletableFuture.runAsync(() -> {
                    try {
                        if (!messages.isEmpty()) {
                            emailDispatcherService.dispatch(
                                    Collections.singletonList(message),
                                    messages.get(0).getLocale()
                            );
                        }
                    } catch (Exception e) {
                        log.error("Error processing message {}: {}", getMessageId(message), e.getMessage());
                    }
                }))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error processing batch of messages", e);
        } finally {
            ack.acknowledge();
        }
    }

    protected abstract UUID getMessageId(T message);
    protected abstract EventType getEventType();
}
