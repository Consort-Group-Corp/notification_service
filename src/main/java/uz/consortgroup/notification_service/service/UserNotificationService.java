package uz.consortgroup.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.NotificationStatus;
import uz.consortgroup.notification_service.entity.UserNotification;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;
import uz.consortgroup.notification_service.repository.UserNotificationRepository;
import uz.consortgroup.notification_service.validator.UserNotificationServiceValidator;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final UserNotificationRepository userNotificationRepository;
    private final UserNotificationServiceValidator userNotificationServiceValidator;

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void saveNewUser(UserRegistrationEvent event) {
        userNotificationServiceValidator.validateUserRegistrationEvent(event);

        UserNotification userNotification = buildNotificationFromEvent(event);

        userNotificationRepository.save(userNotification);

        updateNotificationStatus(event.getEmail(), NotificationStatus.SENT);
    }

    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveNewUser(List<UserRegistrationEvent> events) {
        userNotificationServiceValidator.validateUserRegistrationEvent(events);
        List<UserNotification> notifications = events.stream()
                .map(this::buildNotificationFromEvent)
                .collect(Collectors.toList());

        userNotificationRepository.saveAll(notifications);

        List<String> emails = notifications.stream()
                .map(UserNotification::getEmail)
                .collect(Collectors.toList());
        updateNotificationStatuses(emails, NotificationStatus.SENT);
    }

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void updateNotificationStatus(String email, NotificationStatus status) {
        userNotificationRepository.updateNotificationStatus(email, status);
    }

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void updateNotificationStatuses(List<String> emails, NotificationStatus status) {
        userNotificationRepository.updateNotificationStatuses(emails, status);
    }

    private UserNotification buildNotificationFromEvent(UserRegistrationEvent event) {
        return UserNotification.builder()
                .userId(event.getUserId())
                .language(event.getLanguage())
                .lastName(event.getLastName())
                .firstName(event.getFirstName())
                .middleName(event.getMiddleName())
                .email(event.getEmail())
                .bornDate(event.getBornDate())
                .phoneNumber(event.getPhoneNumber())
                .eventType(event.getEventType())
                .notificationStatus(NotificationStatus.PENDING)
                .communication(event.getCommunication())
                .build();
    }
}


