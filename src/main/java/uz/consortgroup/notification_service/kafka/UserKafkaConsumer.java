package uz.consortgroup.notification_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.dto.KafkaDto;
import uz.consortgroup.notification_service.service.EmailService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserKafkaConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.user-registration}", groupId = "${kafka.consumer-group-id}", containerFactory = "taskKafkaListenerContainerFactory")
    public void consume(@Payload List<KafkaDto> messages, Acknowledgment ack) {
        try {
            log.info("Received messages: {}", messages);
            ack.acknowledge();
            messages.forEach(emailService::sendMail);
        } catch (Exception e) {
            log.error("Error consuming messages: {}", e.getMessage());
        }
    }
}
