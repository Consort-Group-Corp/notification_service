package uz.consortgroup.notification_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;
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
public class VerificationCodeResentEventTest {
    @Mock
    private EmailDispatcherService dispatcherService;

    @Mock
    private Acknowledgment ack;

    private VerificationCodeResentKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new VerificationCodeResentKafkaConsumer(dispatcherService);
    }

    @Test
    void shouldProcessAllMessagesSuccessfully() {
        VerificationCodeResentEvent msg1 = new VerificationCodeResentEvent();
        msg1.setMessageId(101L);
        msg1.setLocale(Locale.ENGLISH);

        VerificationCodeResentEvent msg2 = new VerificationCodeResentEvent();
        msg2.setMessageId(102L);
        msg2.setLocale(Locale.ENGLISH);

        consumer.handleVerificationCodeResentEvents(List.of(msg1, msg2), ack);

        verify(dispatcherService).dispatch(eq(msg1), eq(EventType.VERIFICATION_CODE_SENT), eq(Locale.ENGLISH));
        verify(dispatcherService).dispatch(eq(msg2), eq(EventType.VERIFICATION_CODE_SENT), eq(Locale.ENGLISH));
        verify(ack).acknowledge();
    }

    @Test
    void shouldContinueIfOneMessageFails() {
        VerificationCodeResentEvent msg1 = new VerificationCodeResentEvent();
        msg1.setMessageId(201L);
        msg1.setLocale(Locale.ENGLISH);

        VerificationCodeResentEvent msg2 = new VerificationCodeResentEvent();
        msg2.setMessageId(202L);
        msg2.setLocale(Locale.ENGLISH);

        doThrow(new RuntimeException("Fail")).when(dispatcherService)
                .dispatch(eq(msg1), any(), any());

        consumer.handleVerificationCodeResentEvents(List.of(msg1, msg2), ack);

        verify(dispatcherService).dispatch(eq(msg1), any(), any());
        verify(dispatcherService).dispatch(eq(msg2), any(), any());
        verify(ack).acknowledge();
    }

    @Test
    void shouldIgnoreNullMessages() {
        VerificationCodeResentEvent msg = new VerificationCodeResentEvent();
        msg.setMessageId(301L);
        msg.setLocale(Locale.ENGLISH);

        List<VerificationCodeResentEvent> messages = new ArrayList<>();
        messages.add(null);
        messages.add(msg);

        consumer.handleVerificationCodeResentEvents(messages, ack);

        verify(dispatcherService, times(1)).dispatch(eq(msg), eq(EventType.VERIFICATION_CODE_SENT), eq(Locale.ENGLISH));
        verify(ack).acknowledge();
    }

}
