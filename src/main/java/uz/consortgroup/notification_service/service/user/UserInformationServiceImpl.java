package uz.consortgroup.notification_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
public class UserInformationServiceImpl implements UserInformationService {
    private final UserInformationRepository userInformationRepository;
    private final UserNotificationServiceValidator userNotificationServiceValidator;

    @Override
    @Transactional
    public void saveUserBaseInfo(List<UserRegisteredEvent> events) {
        log.info("Start saving user base info for {} users", events.size());
        userNotificationServiceValidator.validateUserRegistrationEvent(events);

        List<UserInformation> notifications = events.stream()
                .map(event -> UserInformation.builder()
                        .userId(event.getUserId())
                        .language(event.getLanguage())
                        .email(event.getEmail())
                        .build())
                .collect(Collectors.toList());

        userInformationRepository.saveAll(notifications);
        log.info("Successfully saved base user info for {} users", notifications.size());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveUserFullInfo(List<UserProfileUpdateEvent> events) {
        log.info("Start saving full user info for {} events", events.size());
        for (UserProfileUpdateEvent event : events) {
            userInformationRepository.updateUserInfoAndReturn(
                    event.getUserId(),
                    event.getLastName(),
                    event.getFirstName(),
                    event.getMiddleName(),
                    event.getBornDate(),
                    event.getPhoneNumber()
            );
            log.debug("Updated info for user: {}", event.getUserId());
        }
        log.info("Completed updating full user info");
    }

    @Override
    @Transactional
    public List<UUID> findUserIdsByEmails(List<String> emails) {
        log.info("Finding userIds by {} emails", emails.size());
        List<UUID> userIds = userInformationRepository.findUserIdsByEmails(emails);
        log.debug("Found {} userIds", userIds.size());
        return userIds;
    }

    @Override
    public List<UserInformation> findAllByUserIdsInChunks(List<UUID> userIds) {
        log.info("Fetching user info for {} userIds in chunks", userIds.size());

        int chunkSize = 500;
        List<UserInformation> result = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += chunkSize) {
            List<UUID> chunk = userIds.subList(i, Math.min(i + chunkSize, userIds.size()));
            List<UserInformation> users = userInformationRepository.findAllByUserIds(chunk);
            result.addAll(users);
            log.debug("Fetched {} users for chunk [{}-{})", users.size(), i, i + chunkSize);
        }

        log.info("Completed fetching user info. Total records: {}", result.size());
        return result;
    }
}
