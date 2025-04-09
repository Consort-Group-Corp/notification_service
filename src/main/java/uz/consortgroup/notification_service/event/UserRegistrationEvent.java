package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.notification_service.entity.Communication;
import uz.consortgroup.notification_service.entity.EventType;
import uz.consortgroup.notification_service.entity.Language;

import java.time.LocalDate;
import java.util.Locale;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRegistrationEvent implements PersonalizableEmailContent {
    @JsonProperty("messageId")
    private Long messageId;
    @JsonProperty("language")
    private Language language;
    private Long userId;
    private String lastName;
    private String firstName;
    private String middleName;
    private String email;
    private String phoneNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate bornDate;
    private String verificationCode;
    private Locale locale;
    @JsonProperty("communication")
    private Communication communication;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;

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
}