package uz.consortgroup.notification_service.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FcmSender {

    public void send(String fcmToken, String title, String message) {
        Message firebaseMessage = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(message)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(firebaseMessage);
            log.info("FCM message sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message: {}", e.getMessage());
            throw new RuntimeException("FCM send failed", e);
        }
    }
}
