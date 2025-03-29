package uz.consortgroup.notification_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.dto.VerificationKafkaDto;
import uz.consortgroup.notification_service.service.EmailService;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterKafkaConsumer {
    private final Set<Long> processedIds;
    private final EmailService emailService;

    @KafkaListener(
            topics = "${kafka.user-registration}",
            groupId = "${kafka.consumer-group-id}",
            containerFactory = "universalKafkaListenerContainerFactory"
    )
    public void consume(@Payload List<VerificationKafkaDto> messages, Acknowledgment ack) {
        try {
            ack.acknowledge();

            List<VerificationKafkaDto> uniqueMessages = messages.stream()
                    .filter(message -> processedIds.add(message.getMessageId()))
                    .toList();

            if (uniqueMessages.isEmpty()) {
                log.info("All messages are duplicates. Skipping processing.");
                return;
            }

            log.info("Processing unique messages: {}", uniqueMessages);
            uniqueMessages.forEach(emailService::sendMail);

        } catch (Exception e) {
            log.error("Error consuming messages: {}", e.getMessage(), e);
        }
    }
}
