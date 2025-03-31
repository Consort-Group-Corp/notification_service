package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRegistrationEvent implements UserRegistrationEmailContent {
    @JsonProperty("messageId")
    private Long messageId;
    private Long userId;
    private String firstName;
    private String middleName;
    private String email;
    private String verificationCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;

    @Override
    public String getVerificationCode() {
        return this.verificationCode;
    }
}