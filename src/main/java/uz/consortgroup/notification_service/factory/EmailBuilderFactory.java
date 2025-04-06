package uz.consortgroup.notification_service.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.EventType;
import uz.consortgroup.notification_service.message_builder.EmailMessageBuilder;
import uz.consortgroup.notification_service.message_builder.RegistrationEmailBuilder;
import uz.consortgroup.notification_service.message_builder.ResendEmailBuilder;

@Component
@RequiredArgsConstructor
public class EmailBuilderFactory {

    private final RegistrationEmailBuilder registrationBuilder;
    private final ResendEmailBuilder resendBuilder;

    public EmailMessageBuilder getBuilder(EventType type) {
        return switch (type) {
            case USER_REGISTERED -> registrationBuilder;
            case VERIFICATION_CODE_SENT -> resendBuilder;
            default -> throw new IllegalArgumentException("Unsupported email type: " + type);
        };
    }
}
