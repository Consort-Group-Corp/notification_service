package uz.consortgroup.notification_service.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;
import uz.consortgroup.notification_service.message_builder.PasswordResetEmailBuilder;
import uz.consortgroup.notification_service.message_builder.RegistrationEmailBuilder;
import uz.consortgroup.notification_service.message_builder.ResendEmailBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailBuilderFactory {

    private final RegistrationEmailBuilder registrationBuilder;
    private final ResendEmailBuilder resendBuilder;
    private final PasswordResetEmailBuilder passwordResetEmailBuilder;

    public EmailMessageBuilder getBuilder(EventType type) {
        log.info("Resolving EmailMessageBuilder for EventType: {}", type);

        return switch (type) {
            case USER_REGISTERED -> {
                log.debug("Selected RegistrationEmailBuilder for USER_REGISTERED");
                yield registrationBuilder;
            }
            case VERIFICATION_CODE_SENT -> {
                log.debug("Selected ResendEmailBuilder for VERIFICATION_CODE_SENT");
                yield resendBuilder;
            }
            case PASSWORD_RESET_REQUESTED -> {
                log.debug("Selected PasswordResetEmailBuilder for PASSWORD_RESET_REQUESTED");
                yield passwordResetEmailBuilder;
            }
            case USER_PROFILE_UPDATED -> {
                log.warn("No EmailMessageBuilder defined for USER_PROFILE_UPDATED");
                yield null;
            }
            default -> {
                log.error("Unsupported EventType: {}", type);
                throw new IllegalArgumentException("Unsupported email type: " + type);
            }
        };
    }
}
