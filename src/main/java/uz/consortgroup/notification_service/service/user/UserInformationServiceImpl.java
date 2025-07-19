package uz.consortgroup.notification_service.service.user;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserInformationServiceImpl implements UserInformationService {
    private final UserInformationRepository userInformationRepository;
    private final UserNotificationServiceValidator userNotificationServiceValidator;

    @Transactional
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    @Override
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
    @Override
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
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    @AspectAfterReturning
    @Override
    public List<UUID> findUserIdsByEmails(List<String> emails) {
        return userInformationRepository.findUserIdsByEmails(emails);
    }

    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    @AspectAfterReturning
    @Override
    public List<UserInformation> findAllByUserIdsInChunks(List<UUID> userIds) {
        int chunkSize = 500;
        List<UserInformation> result = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += chunkSize) {
            List<UUID> chunk = userIds.subList(i, Math.min(i + chunkSize, userIds.size()));
            result.addAll(userInformationRepository.findAllByUserIds(chunk));
        }

        return result;
    }
}


