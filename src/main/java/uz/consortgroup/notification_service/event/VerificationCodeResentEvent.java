package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VerificationCodeResentEvent implements ResentVerificationEmailContent {
    private Long messageId;
    private Long userId;
    private String email;
    @JsonProperty("language")
    private Language language;
    private String newVerificationCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;

    private Locale locale;

    public Locale getLocale() {
        if (locale == null && language != null) {
            this.locale = new Locale(language.getCode());
        }

        return locale != null ? locale : Locale.ENGLISH;
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
    public String getFirstName() {
        return "";
    }

    @Override
    public String getMiddleName() {
        return "";
    }
}
