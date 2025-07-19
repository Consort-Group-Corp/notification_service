package uz.consortgroup.notification_service.kafka;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.service.email.EmailDispatcherService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class AbstractKafkaConsumerTest {
    @Mock
    private EmailDispatcherService dispatcherService;

    @Mock
    private Acknowledgment ack;

    private TestKafkaConsumer consumer;

    private ExecutorService testExecutor;



    @BeforeEach
    void setUp() {
        testExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        consumer = new TestKafkaConsumer(dispatcherService);
    }

    @Test
    void shouldProcessMessagesConcurrently() throws ExecutionException, InterruptedException {
        TestMessage msg1 = new TestMessage(UUID.randomUUID(), Locale.ENGLISH, "user1@example.com", "1234");
        TestMessage msg2 = new TestMessage(UUID.randomUUID(), Locale.ENGLISH, "user2@example.com", "5678");

        consumer.processBatch(List.of(msg1, msg2), ack);

        verify(dispatcherService, times(2)).dispatch(
                anyList(),
                eq(Locale.ENGLISH)
        );
        verify(ack).acknowledge();
    }

    @Test
    void shouldHandleExceptionsInIndividualMessages() {
        TestMessage msg1 = new TestMessage(UUID.randomUUID(), Locale.ENGLISH, "error@example.com", "0000");
        TestMessage msg2 = new TestMessage(UUID.randomUUID(), Locale.ENGLISH, "user@example.com", "9999");

        doThrow(new RuntimeException("dispatch error"))
                .when(dispatcherService)
                .dispatch(Collections.singletonList(msg1), Locale.ENGLISH);

        consumer.processBatch(List.of(msg1, msg2), ack);

        verify(dispatcherService).dispatch(Collections.singletonList(msg1), Locale.ENGLISH);
        verify(dispatcherService).dispatch(Collections.singletonList(msg2), Locale.ENGLISH);
        verify(ack).acknowledge();
    }

    @AfterEach
    void tearDown() {
        testExecutor.shutdown();
    }


    @Test
    void shouldHandleEmptyBatch() {
        consumer.processBatch(Collections.emptyList(), ack);

        verifyNoMoreInteractions(dispatcherService);
        verify(ack).acknowledge();
    }

    @Test
    void shouldProcessMessagesWithSameLocaleFromFirstMessage() {
        TestMessage msg1 = new TestMessage(UUID.randomUUID(), Locale.ENGLISH, "user1@example.com", "1234");
        TestMessage msg2 = new TestMessage(UUID.randomUUID(), Locale.FRENCH, "user2@example.com", "5678");

        consumer.processBatch(List.of(msg1, msg2), ack);

        verify(dispatcherService).dispatch(
                Collections.singletonList(msg1),
                Locale.ENGLISH
        );
        verify(dispatcherService).dispatch(
                Collections.singletonList(msg2),
                Locale.ENGLISH
        );
        verify(ack).acknowledge();
    }

    static class TestKafkaConsumer extends AbstractKafkaConsumer<TestMessage> {
        protected TestKafkaConsumer(EmailDispatcherService emailDispatcherService) {
            super(emailDispatcherService);
        }

        @Override
        protected UUID getMessageId(TestMessage message) {
            return message.getMessageId();
        }

        @Override
        protected EventType getEventType() {
            return EventType.USER_REGISTERED;
        }
    }

    static class TestMessage implements EmailContent {
        private final UUID messageId;
        private final Locale locale;
        private final String email;
        private final String verificationCode;

        public TestMessage(UUID messageId, Locale locale, String email, String verificationCode) {
            this.messageId = messageId;
            this.locale = locale;
            this.email = email;
            this.verificationCode = verificationCode;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getVerificationCode() {
            return verificationCode;
        }

        @Override
        public EventType getEventType() {
            return EventType.USER_REGISTERED;
        }

        public UUID getMessageId() {
            return messageId;
        }
    }
}