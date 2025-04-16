package uz.consortgroup.notification_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.ProfileUpdateProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserProfileUpdateKafkaConsumerTest {
    @Mock
    private EmailDispatcherService dispatcherService;
    @Mock
    private ProfileUpdateProcessor processor;
    @Mock
    private Acknowledgment ack;

    private UserProfileUpdateKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new UserProfileUpdateKafkaConsumer(dispatcherService, processor);
    }

    @Test
    void shouldProcessValidUserRegistrationEvents() {
        UserProfileUpdateEvent msg1 = createTestEvent(1L);
        UserProfileUpdateEvent msg2 = createTestEvent(2L);

        List<UserProfileUpdateEvent> messages = List.of(msg1, msg2);

        consumer.handleUserRegistrationEvents(messages, ack);

        verify(processor).process(messages);
        verify(ack).acknowledge();
    }

    @Test
    void shouldThrowExceptionWhenProcessorFails() {
        UserProfileUpdateEvent msg1 = createTestEvent(1L);
        UserProfileUpdateEvent msg2 = createTestEvent(2L);
        List<UserProfileUpdateEvent> messages = List.of(msg1, msg2);

        doThrow(new RuntimeException("Processor error"))
                .when(processor)
                .process(messages);

        assertThrows(RuntimeException.class, () -> {
            consumer.handleUserRegistrationEvents(messages, ack);
        });

        verify(processor).process(messages);

        verify(ack, never()).acknowledge();
    }

    @Test
    void shouldIgnoreNullMessages() {
        UserProfileUpdateEvent msg = createTestEvent(1L);

        List<UserProfileUpdateEvent> messages = new ArrayList<>();
        messages.add(null);
        messages.add(msg);

        consumer.handleUserRegistrationEvents(messages, ack);

        verify(processor).process(messages);
        verify(ack).acknowledge();
    }

    private UserProfileUpdateEvent createTestEvent(Long messageId) {
        UserProfileUpdateEvent event = new UserProfileUpdateEvent();
        event.setMessageId(messageId);
        return event;
    }
}