package uz.consortgroup.notification_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.repository.UserProfileUpdateLogRepository;
import uz.consortgroup.notification_service.service.user.UserInformationServiceImpl;
import uz.consortgroup.notification_service.service.user.UserProfileUpdateLogServiceImpl;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileUpdateLogServiceImplTest {

    @Mock
    private UserProfileUpdateLogRepository userProfileUpdateLogRepository;

    @Mock
    private UserInformationServiceImpl userInformationServiceImpl;

    @InjectMocks
    private UserProfileUpdateLogServiceImpl userProfileUpdateLogServiceImpl;

    private UserInformation testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = UserInformation.builder()
                .userId(testUserId)
                .email("test@example.com")
                .build();
    }

    @Test
    void logUserProfileUpdate_ShouldSaveLogs() {
        List<UUID> userIds = List.of(testUserId);
        EventType eventType = EventType.USER_PROFILE_UPDATED;

        when(userInformationServiceImpl.findAllByUserIdsInChunks(userIds)).thenReturn(List.of(testUser));

        userProfileUpdateLogServiceImpl.logUserProfileUpdate(userIds, eventType);

        verify(userProfileUpdateLogRepository).saveAll(anyList());
    }

    @Test
    void logUserProfileUpdate_WhenUserNotFound_ShouldNotSaveLogs() {
        List<UUID> userIds = List.of(testUserId);
        EventType eventType = EventType.USER_PROFILE_UPDATED;

        when(userInformationServiceImpl.findAllByUserIdsInChunks(userIds)).thenReturn(List.of());

        userProfileUpdateLogServiceImpl.logUserProfileUpdate(userIds, eventType);

        verify(userProfileUpdateLogRepository, never()).saveAll(anyList());
    }

    @Test
    void logUserProfileUpdate_ShouldCreateLogWithCorrectFields() {
        List<UUID> userIds = List.of(testUserId);
        EventType eventType = EventType.USER_PROFILE_UPDATED;

        when(userInformationServiceImpl.findAllByUserIdsInChunks(userIds)).thenReturn(List.of(testUser));

        userProfileUpdateLogServiceImpl.logUserProfileUpdate(userIds, eventType);
    }

    @Test
    void logUserProfileUpdate_WhenListIsEmpty_ShouldNotSaveLogs() {
        userProfileUpdateLogServiceImpl.logUserProfileUpdate(List.of(), EventType.USER_PROFILE_UPDATED);

        verify(userProfileUpdateLogRepository, never()).saveAll(anyList());
    }
}
