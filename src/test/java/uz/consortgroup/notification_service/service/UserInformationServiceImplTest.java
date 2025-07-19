package uz.consortgroup.notification_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.notification_service.entity.UserInformation;
import uz.consortgroup.notification_service.event.UserProfileUpdateEvent;
import uz.consortgroup.notification_service.event.UserRegisteredEvent;
import uz.consortgroup.notification_service.repository.UserInformationRepository;
import uz.consortgroup.notification_service.service.user.UserInformationServiceImpl;
import uz.consortgroup.notification_service.validator.UserNotificationServiceValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInformationServiceImplTest {

    @Mock
    private UserInformationRepository userInformationRepository;

    @Mock
    private UserNotificationServiceValidator validator;

    @InjectMocks
    private UserInformationServiceImpl userInformationServiceImpl;

    @Test
    void saveUserBaseInfo_ShouldSaveAllUsers() {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setUserId(UUID.randomUUID());
        event.setEmail("test@example.com");
        event.setLanguage(Language.ENGLISH);

        List<UserRegisteredEvent> events = List.of(event);

        userInformationServiceImpl.saveUserBaseInfo(events);

        verify(validator).validateUserRegistrationEvent(events);
        verify(userInformationRepository).saveAll(anyList());
    }

    @Test
    void saveUserFullInfo_ShouldUpdateAllUsers() {
        UserProfileUpdateEvent event = new UserProfileUpdateEvent();
        event.setUserId(UUID.randomUUID());
        event.setFirstName("John");
        event.setLastName("Doe");
        event.setMiddleName("Middle");
        event.setBornDate(LocalDate.of(1990, 1, 1));
        event.setPhoneNumber("+1234567890");

        List<UserProfileUpdateEvent> events = List.of(event);

        userInformationServiceImpl.saveUserFullInfo(events);

        verify(userInformationRepository, times(events.size())).updateUserInfoAndReturn(
                any(UUID.class),
                anyString(),
                anyString(),
                anyString(),
                any(LocalDate.class),
                anyString());
    }

    @Test
    void findUserIdsByEmails_ShouldReturnUserIds() {
        List<String> emails = List.of("test@example.com");
        when(userInformationRepository.findUserIdsByEmails(emails))
                .thenReturn(List.of(UUID.randomUUID()));

        List<UUID> result = userInformationServiceImpl.findUserIdsByEmails(emails);

        assertNotNull(result);
        verify(userInformationRepository).findUserIdsByEmails(emails);
    }

    @Test
    void findAllByUserIds_ShouldReturnUserInformation() {
        List<UUID> userIds = List.of(UUID.randomUUID());
        when(userInformationRepository.findAllByUserIds(userIds))
                .thenReturn(List.of(new UserInformation()));

        List<UserInformation> result = userInformationServiceImpl.findAllByUserIdsInChunks(userIds);

        assertNotNull(result);
        verify(userInformationRepository).findAllByUserIds(userIds);
    }
}