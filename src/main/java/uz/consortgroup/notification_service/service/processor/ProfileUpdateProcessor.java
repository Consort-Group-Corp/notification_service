package uz.consortgroup.notification_service.service.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EventProcessor;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.service.user.UserInformationService;
import uz.consortgroup.notification_service.service.user.UserProfileUpdateLogService;
import uz.consortgroup.notification_service.validator.ProfileUpdateProcessorValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
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
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public void process(List<UserProfileUpdateEvent> events) {
        profileUpdateProcessorValidator.validateEvents(events);
        userInformationService.saveUserFullInfo(events);
        userProfileUpdateLogService.logUserProfileUpdate(events.stream()
                .map(UserProfileUpdateEvent::getUserId).toList(), EventType.USER_PROFILE_UPDATED);
    }
}
