package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.entity.enumeration.Language;

import java.util.Locale;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent implements EmailContent, PersonalizableEmailContent {
    @JsonProperty("messageId")
    private Long messageId;
    @JsonProperty("language")
    private Language language;
    private UUID userId;
    private String email;
    private String verificationCode;
    @JsonProperty("eventType")
    private EventType eventType;
    private Locale locale;

    @Override
    public Locale getLocale() {
        return resolveLocale(language, locale);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getVerificationCode() {
        return verificationCode;
    }

    @Override
    public EventType getEventType() {
        return EventType.USER_REGISTERED;
    }
}
