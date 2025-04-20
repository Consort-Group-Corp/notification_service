package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.entity.enumeration.Language;

import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VerificationCodeResentEvent implements EmailContent, PersonalizableEmailContent {
    private UUID messageId;
    private UUID userId;
    private String email;
    private String newVerificationCode;
    @JsonProperty("language")
    private Language language;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
        return newVerificationCode;
    }

    @Override
    public UUID getMessageId() {
        return messageId;
    }
}
