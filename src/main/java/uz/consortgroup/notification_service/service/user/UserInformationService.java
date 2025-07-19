package uz.consortgroup.notification_service.service.user;

import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;

import java.util.List;
import java.util.UUID;

public interface UserInformationService {
    void saveUserBaseInfo(List<UserRegisteredEvent> event);
    void saveUserFullInfo(List<UserProfileUpdateEvent> events);
    List<UUID> findUserIdsByEmails(List<String> emails);
    List<UserInformation> findAllByUserIdsInChunks(List<UUID> userIds);
}
