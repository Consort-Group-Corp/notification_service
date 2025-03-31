package uz.consortgroup.notification_service.kafka;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.service.EmailService;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public abstract class AbstractKafkaConsumer<T> {
    protected final Set<Long> processedMessageIds;
    protected final EmailService emailService;

    protected void processBatch(List<T> messages, Acknowledgment acknowledgment, MessageProcessor<T> processor) {
        try {
            if (messages == null || messages.isEmpty()) {
                log.debug("Received empty batch, acknowledging");
                acknowledgment.acknowledge();
                return;
            }

            messages.stream()
                    .filter(Objects::nonNull)
                    .forEach(message -> {
                        try {
                            processor.process(message);
                        } catch (Exception e) {
                            log.error("Message processing failed: {}", e.getMessage());
                        }
                    });

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Fatal batch processing error: {}", e.getMessage());
        }
    }

    protected abstract Long getMessageId(T message);
}
