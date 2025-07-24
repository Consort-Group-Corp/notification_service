package uz.consortgroup.notification_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.entity.UserProfileUpdateLog;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.repository.UserProfileUpdateLogRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileUpdateLogServiceImpl implements UserProfileUpdateLogService {

    private final UserProfileUpdateLogRepository userProfileUpdateLogRepository;
    private final UserInformationService userInformationService;

    @Transactional
    @Override
    public void logUserProfileUpdate(List<UUID> userIds, EventType eventType) {
        log.info("Logging profile update for {} users with event type {}", userIds.size(), eventType);

        Map<UUID, UserInformation> userById = userInformationService.findAllByUserIdsInChunks(userIds).stream()
                .collect(Collectors.toMap(UserInformation::getUserId, Function.identity()));

        List<UserProfileUpdateLog> logs = userIds.stream()
                .map(id -> {
                    UserInformation user = userById.get(id);
                    if (user == null) {
                        log.warn("User not found for id {}", id);
                        return null;
                    }

                    return UserProfileUpdateLog.builder()
                            .userInformation(user)
                            .eventType(eventType)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        if (!logs.isEmpty()) {
            userProfileUpdateLogRepository.saveAll(logs);
            log.info("Saved {} user profile update logs", logs.size());
        } else {
            log.info("No profile update logs were saved because no users matched");
        }
    }
}
