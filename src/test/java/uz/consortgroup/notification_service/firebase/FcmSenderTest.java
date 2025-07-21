package uz.consortgroup.notification_service.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FcmSenderTest {

    private FcmSender fcmSender;
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setup() {
        fcmSender = new FcmSender();
        firebaseMessaging = mock(FirebaseMessaging.class);
    }

    @Test
    void shouldSendMessageSuccessfully() throws Exception {
        try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class))).thenReturn("mock-response");

            fcmSender.send("test-token", "Test Title", "Test Message");

            verify(firebaseMessaging, times(1)).send(any(Message.class));
        }
    }

    @Test
    void shouldThrowExceptionWhenSendFails() throws Exception {
        try (MockedStatic<FirebaseMessaging> mockedStatic = mockStatic(FirebaseMessaging.class)) {
            mockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);

            when(firebaseMessaging.send(any(Message.class)))
                    .thenThrow(new RuntimeException("Mocked FirebaseMessagingException"));

            org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                    fcmSender.send("test-token", "Test Title", "Test Message"));
        }
    }
}
