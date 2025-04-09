package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.entity.Language;

import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VerificationCodeResentEvent implements EmailContent {
    private Long messageId;
    private Long userId;
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
    public Long getMessageId() {
        return messageId;
    }
}
