package uz.consortgroup.notification_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceValidatorTest {

    private final UserNotificationServiceValidator validator = new UserNotificationServiceValidator();

    @Test
    void validateUserRegistrationEvent_ValidEvent_ShouldPass() {
        UserRegistrationEvent event = UserRegistrationEvent.builder()
                .firstName("John")
                .middleName("M.")
                .lastName("Doe")
                .build();

        assertDoesNotThrow(() -> validator.validateUserRegistrationEvent(event));
    }

    @Test
    void validateUserRegistrationEventList_ValidList_ShouldPass() {
        List<UserRegistrationEvent> events = List.of(
                UserRegistrationEvent.builder().firstName("A").middleName("B").lastName("C").build(),
                UserRegistrationEvent.builder().firstName("X").middleName("Y").lastName("Z").build()
        );

        assertDoesNotThrow(() -> validator.validateUserRegistrationEvent(events));
    }

    @Test
    void validateUserRegistrationEvent_NullEvent_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUserRegistrationEvent((UserRegistrationEvent) null));

        assertEquals("UserRegistrationEvent must not be null", exception.getMessage());
    }

    @Test
    void validateUserRegistrationEvent_BlankFirstName_ShouldThrow() {
        UserRegistrationEvent event = UserRegistrationEvent.builder()
                .firstName(" ")
                .middleName("M")
                .lastName("L")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUserRegistrationEvent(event));

        assertEquals("First name must be provided", exception.getMessage());
    }

    @Test
    void validateUserRegistrationEvent_BlankLastName_ShouldThrow() {
        UserRegistrationEvent event = UserRegistrationEvent.builder()
                .firstName("J")
                .middleName("M")
                .lastName(" ")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUserRegistrationEvent(event));

        assertEquals("Last name must be provided", exception.getMessage());
    }

    @Test
    void validateUserRegistrationEvent_BlankMiddleName_ShouldThrow() {
        UserRegistrationEvent event = UserRegistrationEvent.builder()
                .firstName("J")
                .middleName(" ")
                .lastName("D")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUserRegistrationEvent(event));

        assertEquals("Middle name must be provided", exception.getMessage());
    }

    @Test
    void validateUserRegistrationEventList_NullList_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUserRegistrationEvent((List<UserRegistrationEvent>) null));

        assertEquals("UserRegistrationEvent list must not be null or empty", exception.getMessage());
    }

    @Test
    void validateUserRegistrationEventList_EmptyList_ShouldThrow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUserRegistrationEvent(List.of()));

        assertEquals("UserRegistrationEvent list must not be null or empty", exception.getMessage());
    }

    @Test
    void validateUserRegistrationEventList_InvalidEventInList_ShouldThrow() {
        List<UserRegistrationEvent> events = List.of(
                UserRegistrationEvent.builder().firstName(" ").middleName("X").lastName("Y").build()
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateUserRegistrationEvent(events));

        assertEquals("First name must be provided", exception.getMessage());
    }
}
