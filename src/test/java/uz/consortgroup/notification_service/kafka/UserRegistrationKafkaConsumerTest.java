package uz.consortgroup.notification_service.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;
import uz.consortgroup.notification_service.service.EmailDispatcherService;
import uz.consortgroup.notification_service.service.processor.UserRegistrationProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationKafkaConsumerTest {
    @Mock
    private EmailDispatcherService dispatcherService;

    @Mock
    private UserRegistrationProcessor userRegistrationProcessor;

    @Mock
    private Acknowledgment ack;

    private UserRegistrationKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new UserRegistrationKafkaConsumer(dispatcherService, userRegistrationProcessor);
    }

    @Test
    void shouldProcessValidUserRegistrationEvents() {
        UserRegistrationEvent msg1 = new UserRegistrationEvent();
        UserRegistrationEvent msg2 = new UserRegistrationEvent();

        List<UserRegistrationEvent> messages = List.of(msg1, msg2);

        consumer.handleUserRegistrationEvents(messages, ack);

        verify(dispatcherService, times(2)).dispatch(any(), eq(EventType.USER_REGISTERED), eq(Locale.ENGLISH));
        verify(userRegistrationProcessor).handleUserRegistrationEvents(messages);
        verify(ack).acknowledge();
    }


    @Test
    void shouldContinueProcessingIfOneMessageFails() {
        UserRegistrationEvent msg1 = new UserRegistrationEvent();
        msg1.setMessageId(1L);

        UserRegistrationEvent msg2 = new UserRegistrationEvent();
        msg2.setMessageId(2L);

        doThrow(new RuntimeException("Error in dispatcher"))
                .when(dispatcherService)
                .dispatch(eq(msg1), any(), any());

        consumer.handleUserRegistrationEvents(List.of(msg1, msg2), ack);

        verify(dispatcherService).dispatch(eq(msg1), any(), any());
        verify(dispatcherService).dispatch(eq(msg2), any(), any());
        verify(userRegistrationProcessor).handleUserRegistrationEvents(any());
        verify(ack).acknowledge();
    }

    @Test
    void shouldIgnoreNullMessages() {
        UserRegistrationEvent msg = new UserRegistrationEvent();

        List<UserRegistrationEvent> messages = new ArrayList<>();
        messages.add(null);
        messages.add(msg);

        consumer.handleUserRegistrationEvents(messages, ack);

        verify(dispatcherService, times(1)).dispatch(eq(msg), any(), any());
        verify(userRegistrationProcessor).handleUserRegistrationEvents(messages);
        verify(ack).acknowledge();
    }
}
