package uz.consortgroup.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterReturning;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.repository.UserInformationRepository;
import uz.consortgroup.notification_service.validator.UserNotificationServiceValidator;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserInformationService {
    private final UserInformationRepository userInformationRepository;
    private final UserNotificationServiceValidator userNotificationServiceValidator;

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void saveUserBaseInfo(List<UserRegisteredEvent> event) {
        userNotificationServiceValidator.validateUserRegistrationEvent(event);

        List<UserInformation> notifications = event.stream()
                .map(userRegisteredEvent -> UserInformation.builder()
                        .userId(userRegisteredEvent.getUserId())
                        .language(userRegisteredEvent.getLanguage())
                        .email(userRegisteredEvent.getEmail())
                        .build())
                .collect(Collectors.toList());

        userInformationRepository.saveAll(notifications);
    }

    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveUserFullInfo(List<UserProfileUpdateEvent> events) {
        events.forEach(event -> {
            userInformationRepository.updateUserInfoAndReturn(
                    event.getUserId(),
                    event.getLastName(),
                    event.getFirstName(),
                    event.getMiddleName(),
                    event.getBornDate(),
                    event.getPhoneNumber()
            );
        });
    }

    @Transactional
    public List<UUID> findUserIdsByEmails(List<String> emails) {
        return userInformationRepository.findUserIdsByEmails(emails);
    }


    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    @AspectAfterReturning
    public List<UserInformation> findAllByUserIds(List<UUID> userId) {
        return userInformationRepository.findAllByUserIds(userId);
    }
}


