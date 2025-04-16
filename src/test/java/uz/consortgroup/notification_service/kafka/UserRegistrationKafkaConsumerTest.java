package uz.consortgroup.notification_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.UserRegistrationProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationKafkaConsumerTest {
    @Mock
    private EmailDispatcherService dispatcherService;
    @Mock
    private UserRegistrationProcessor processor;
    @Mock
    private Acknowledgment ack;

    private UserRegistrationKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new UserRegistrationKafkaConsumer(dispatcherService, processor);
    }

    @Test
    void shouldProcessAllMessagesSuccessfully() throws Exception {
        UserRegisteredEvent msg1 = createTestEvent(1L);
        UserRegisteredEvent msg2 = createTestEvent(2L);
        List<UserRegisteredEvent> messages = List.of(msg1, msg2);

        CountDownLatch latch = new CountDownLatch(2);
        doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(dispatcherService).dispatch(anyList(), any());

        consumer.handleUserRegistrationEvents(messages, ack);

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Not all messages processed");
        verify(processor).process(messages);
        verify(ack).acknowledge();
    }

    @Test
    void shouldContinueOnProcessingError() throws Exception {
        UserRegisteredEvent msg1 = createTestEvent(1L);
        UserRegisteredEvent msg2 = createTestEvent(2L);
        List<UserRegisteredEvent> messages = List.of(msg1, msg2);

        doThrow(new RuntimeException("Dispatch error"))
                .when(dispatcherService)
                .dispatch(anyList(), any());

        consumer.handleUserRegistrationEvents(messages, ack);

        verify(dispatcherService, times(2)).dispatch(anyList(), any());
        verify(processor).process(messages);
        verify(ack).acknowledge();
    }

    @Test
    void shouldIgnoreNullMessages() throws Exception {
        UserRegisteredEvent msg = createTestEvent(1L);
        List<UserRegisteredEvent> messages = new ArrayList<>();
        messages.add(null);
        messages.add(msg);

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(processor).process(anyList());

        consumer.handleUserRegistrationEvents(messages, ack);

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Processor not called");
        verify(processor).process(messages);
        verify(ack).acknowledge();
    }

    private UserRegisteredEvent createTestEvent(Long messageId) {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setMessageId(messageId);
        event.setLocale(Locale.ENGLISH);
        return event;
    }
}