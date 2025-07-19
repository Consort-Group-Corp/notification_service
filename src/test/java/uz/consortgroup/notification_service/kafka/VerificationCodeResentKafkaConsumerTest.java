package uz.consortgroup.notification_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.VerificationCodeProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class VerificationCodeResentKafkaConsumerTest {
    @Mock
    private EmailDispatcherService dispatcherService;

    @Mock
    private VerificationCodeProcessor processor;

    @Mock
    private Acknowledgment ack;

    private VerificationCodeResentKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new VerificationCodeResentKafkaConsumer(dispatcherService, processor);
    }

    @Test
    void shouldProcessAllMessagesSuccessfully() throws Exception {
        VerificationCodeResentEvent msg1 = createTestEvent(UUID.randomUUID());
        VerificationCodeResentEvent msg2 = createTestEvent(UUID.randomUUID());
        List<VerificationCodeResentEvent> messages = List.of(msg1, msg2);

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(processor).process(anyList());

        consumer.handleVerificationCodeResentEvents(messages, ack);

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Processor not called");

        verify(dispatcherService, times(2)).dispatch(anyList(), any());
        verify(ack).acknowledge();
    }

    @Test
    void shouldNotCallDispatchWhenProcessorFails() throws Exception {
        VerificationCodeResentEvent msg1 = createTestEvent(UUID.randomUUID());
        VerificationCodeResentEvent msg2 = createTestEvent(UUID.randomUUID());
        List<VerificationCodeResentEvent> messages = List.of(msg1, msg2);

        doThrow(new RuntimeException("Processor error"))
                .when(processor)
                .process(messages);

        try {
            consumer.handleVerificationCodeResentEvents(messages, ack);
        } catch (RuntimeException e) {

        }

        verify(processor).process(messages);
        verify(dispatcherService, never()).dispatch(anyList(), any());
        verify(ack, never()).acknowledge();
    }

    @Test
    void shouldProcessMessagesWithNullValues() throws Exception {
        VerificationCodeResentEvent msg = createTestEvent(UUID.randomUUID());
        List<VerificationCodeResentEvent> messages = new ArrayList<>();
        messages.add(null);
        messages.add(msg);

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(processor).process(messages);

        consumer.handleVerificationCodeResentEvents(messages, ack);

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Processor not called");

        verify(processor).process(messages);

        verify(ack).acknowledge();
    }

    private VerificationCodeResentEvent createTestEvent(UUID messageId) {
        VerificationCodeResentEvent event = new VerificationCodeResentEvent();
        event.setMessageId(messageId);
        event.setLocale(Locale.ENGLISH);
        return event;
    }
}