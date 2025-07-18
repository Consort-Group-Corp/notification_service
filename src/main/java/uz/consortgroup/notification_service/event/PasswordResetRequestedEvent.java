package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.notification_service.entity.enumeration.EventType;

import java.util.Locale;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PasswordResetRequestedEvent implements EmailContent {
    private UUID messageId;
    private UUID userId;
    private String email;
    private String token;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;
    private String resetLink;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Language language;
    private Locale locale;

    @Override
    public EventType getEventType() {
        return EventType.PASSWORD_RESET_REQUESTED;
    }

    @Override
    public String getResetLink() {
        return resetLink;
    }

    @Override
    public Locale getLocale() {
        return resolveLocale(language, locale);
    }

    @Override
    public UUID getMessageId() {
        return messageId;
    }
}
