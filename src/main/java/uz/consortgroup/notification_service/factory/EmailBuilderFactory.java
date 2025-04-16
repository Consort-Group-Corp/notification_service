package uz.consortgroup.notification_service.factory;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;
import uz.consortgroup.notification_service.message_builder.PasswordResetEmailBuilder;
import uz.consortgroup.notification_service.message_builder.RegistrationEmailBuilder;
import uz.consortgroup.notification_service.message_builder.ResendEmailBuilder;

@Component
@RequiredArgsConstructor
public class EmailBuilderFactory {

    private final RegistrationEmailBuilder registrationBuilder;
    private final ResendEmailBuilder resendBuilder;
    private final PasswordResetEmailBuilder passwordResetEmailBuilder;

    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AfterThrowing
    @AfterReturning
    public EmailMessageBuilder getBuilder(EventType type) {
        return switch (type) {
            case USER_REGISTERED -> registrationBuilder;
            case VERIFICATION_CODE_SENT -> resendBuilder;
            case USER_PROFILE_UPDATED -> null;
            case PASSWORD_RESET_REQUESTED -> passwordResetEmailBuilder;
            default -> throw new IllegalArgumentException("Unsupported email type: " + type);
        };
    }
}
