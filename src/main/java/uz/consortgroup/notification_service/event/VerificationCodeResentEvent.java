package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VerificationCodeResentEvent implements ResentVerificationEmailContent {
    private Long messageId;
    private Long userId;
    private String email;
    private String newVerificationCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;

    @Override
    public String getVerificationCode() {
        return this.newVerificationCode;
    }
}
