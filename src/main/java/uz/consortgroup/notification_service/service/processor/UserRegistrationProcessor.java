package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;
import uz.consortgroup.notification_service.service.UserNotificationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRegistrationProcessor {
    private final UserNotificationService userNotificationService;

    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @Transactional
    public void handleUserRegistrationEvents(List<UserRegistrationEvent> events) {
        userNotificationService.saveNewUser(events);
    }
}
