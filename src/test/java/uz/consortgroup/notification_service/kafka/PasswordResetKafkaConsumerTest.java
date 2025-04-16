package uz.consortgroup.notification_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.PasswordResetRequestProcessor;
import uz.consortgroup.notification_service.validator.PasswordTokenValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PasswordResetKafkaConsumerTest {
    @Mock
    private EmailDispatcherService dispatcherService;

    @Mock
    private PasswordTokenValidator passwordTokenValidator;

    @Mock
    private PasswordResetRequestProcessor processor;

    @Mock
    private Acknowledgment ack;

    private PasswordResetKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new PasswordResetKafkaConsumer(dispatcherService, passwordTokenValidator, processor);
    }

    @Test
    void shouldProcessAllMessagesSuccessfully() throws Exception {
        PasswordResetRequestedEvent msg1 = createTestEvent(1L);
        PasswordResetRequestedEvent msg2 = createTestEvent(2L);
        List<PasswordResetRequestedEvent> messages = List.of(msg1, msg2);

        CountDownLatch latch = new CountDownLatch(3);

        doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(passwordTokenValidator).validateTokensAsync(anyList());

        doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(passwordTokenValidator).getExpirationDatesFromTokensAsync(anyList());

        doAnswer(inv -> {
            latch.countDown();
            return null;
        }).when(processor).process(anyList());

        consumer.handleUserRegistrationEvents(messages, ack);

        assertTrue(latch.await(1, TimeUnit.SECONDS), "Not all operations completed");
        verify(passwordTokenValidator).validateTokensAsync(messages);
        verify(passwordTokenValidator).getExpirationDatesFromTokensAsync(messages);
        verify(processor).process(messages);
        verify(dispatcherService, times(2)).dispatch(anyList(), any());
        verify(ack).acknowledge();
    }

    @Test
    void shouldContinueOnProcessingError() {
        PasswordResetRequestedEvent msg1 = createTestEvent(1L);
        PasswordResetRequestedEvent msg2 = createTestEvent(2L);
        List<PasswordResetRequestedEvent> messages = List.of(msg1, msg2);

        doNothing().when(passwordTokenValidator).validateTokensAsync(anyList());
        doNothing().when(passwordTokenValidator).getExpirationDatesFromTokensAsync(anyList());

        doThrow(new RuntimeException("Processor error"))
                .when(processor)
                .process(anyList());

        try {
            consumer.handleUserRegistrationEvents(messages, ack);
        } catch (RuntimeException e) {
            if (!"Processor error".equals(e.getMessage())) {
                throw e;
            }
        }

        verify(passwordTokenValidator).validateTokensAsync(messages);
        verify(passwordTokenValidator).getExpirationDatesFromTokensAsync(messages);

        verify(processor).process(messages);

        verify(dispatcherService, never()).dispatch(anyList(), any());

        verify(ack, never()).acknowledge();
    }

    @Test
    void shouldIgnoreNullMessages() {
        PasswordResetRequestedEvent msg = createTestEvent(1L);
        List<PasswordResetRequestedEvent> messages = new ArrayList<>();
        messages.add(null);
        messages.add(msg);

        doNothing().when(passwordTokenValidator).validateTokensAsync(anyList());
        doNothing().when(passwordTokenValidator).getExpirationDatesFromTokensAsync(anyList());
        doNothing().when(processor).process(anyList());

        consumer.handleUserRegistrationEvents(messages, ack);

        verify(passwordTokenValidator).validateTokensAsync(argThat(list -> list.size() == 2));
        verify(passwordTokenValidator).getExpirationDatesFromTokensAsync(argThat(list -> list.size() == 2));

        verify(processor).process(argThat(list -> list.size() == 2));

        verify(dispatcherService, never()).dispatch(anyList(), any());

        verify(ack).acknowledge();
    }

    private PasswordResetRequestedEvent createTestEvent(Long messageId) {
        PasswordResetRequestedEvent event = new PasswordResetRequestedEvent();
        event.setMessageId(messageId);
        event.setLocale(Locale.ENGLISH);
        return event;
    }
}