package uz.consortgroup.notification_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AbstractKafkaConsumerTest {
    @Mock
    private EmailDispatcherService dispatcherService;

    @Mock
    private Acknowledgment ack;

    private TestKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new TestKafkaConsumer(dispatcherService);
    }

    @Test
    void shouldProcessMessagesSuccessfully() {
        TestMessage msg1 = new TestMessage(1L, Locale.ENGLISH, "user1@example.com", "1234");
        TestMessage msg2 = new TestMessage(2L, Locale.ENGLISH, "user2@example.com", "5678");

        consumer.processBatch(List.of(msg1, msg2), ack);

        verify(dispatcherService, times(2)).dispatch(any(), eq(EventType.USER_REGISTERED), eq(Locale.ENGLISH));
        verify(ack).acknowledge();
    }

    @Test
    void shouldLogErrorAndContinueOnException() {
        TestMessage msg1 = new TestMessage(1L, Locale.ENGLISH, "error@example.com", "0000");
        TestMessage msg2 = new TestMessage(2L, Locale.ENGLISH, "user@example.com", "9999");

        doThrow(new RuntimeException("dispatch error"))
                .when(dispatcherService)
                .dispatch(eq(msg1), eq(EventType.USER_REGISTERED), eq(Locale.ENGLISH));

        consumer.processBatch(List.of(msg1, msg2), ack);

        verify(dispatcherService).dispatch(eq(msg1), eq(EventType.USER_REGISTERED), eq(Locale.ENGLISH));
        verify(dispatcherService).dispatch(eq(msg2), eq(EventType.USER_REGISTERED), eq(Locale.ENGLISH));
        verify(ack).acknowledge();
    }

    @Test
    void shouldIgnoreNullMessages() {
        TestMessage msg = new TestMessage(1L, Locale.ENGLISH, "user@example.com", "1111");

        List<TestMessage> list = new ArrayList<>();
        list.add(null);
        list.add(msg);

        consumer.processBatch(list, ack);

        verify(dispatcherService, times(1)).dispatch(any(), any(), any());
        verify(ack).acknowledge();
    }


    static class TestKafkaConsumer extends AbstractKafkaConsumer<TestMessage> {
        protected TestKafkaConsumer(EmailDispatcherService emailDispatcherService) {
            super(emailDispatcherService);
        }

        @Override
        protected Long getMessageId(TestMessage message) {
            return message.getMessageId();
        }

        @Override
        protected EventType getEventType() {
            return EventType.USER_REGISTERED;
        }
    }

    static class TestMessage implements EmailContent {
        private final Long messageId;
        private final Locale locale;
        private final String email;
        private final String verificationCode;

        public TestMessage(Long messageId, Locale locale, String email, String verificationCode) {
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

        public Long getMessageId() {
            return messageId;
        }
    }
}
