package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EventProcessor;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.service.user.UserInformationService;
import uz.consortgroup.notification_service.service.user.UserProfileUpdateLogService;
import uz.consortgroup.notification_service.validator.ProfileUpdateProcessorValidator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileUpdateProcessor implements EventProcessor<UserProfileUpdateEvent> {

    private final UserInformationService userInformationService;
    private final UserProfileUpdateLogService userProfileUpdateLogService;
    private final ProfileUpdateProcessorValidator profileUpdateProcessorValidator;

    @Override
    public boolean canHandle(EventType eventType) {
        return eventType == EventType.USER_PROFILE_UPDATED;
    }

    @Override
    @Transactional
    public void process(List<UserProfileUpdateEvent> events) {
        if (events == null || events.isEmpty()) {
            log.warn("No user profile update events to process");
            return;
        }

        log.info("Processing {} USER_PROFILE_UPDATED events", events.size());

        profileUpdateProcessorValidator.validateEvents(events);

        log.debug("Saving user information");
        userInformationService.saveUserFullInfo(events);

        List<UUID> userIds = events.stream()
                .map(UserProfileUpdateEvent::getUserId)
                .toList();

        log.debug("Logging user profile updates for {} users", userIds.size());
        userProfileUpdateLogService.logUserProfileUpdate(userIds, EventType.USER_PROFILE_UPDATED);

        log.info("Successfully processed USER_PROFILE_UPDATED events");
    }
}
