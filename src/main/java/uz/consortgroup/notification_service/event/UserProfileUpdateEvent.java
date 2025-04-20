package uz.consortgroup.notification_service.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.consortgroup.notification_service.entity.enumeration.EventType;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserProfileUpdateEvent implements EmailContent {
    @JsonProperty("messageId")
    private UUID messageId;
    private UUID userId;
    private String lastName;
    private String firstName;
    private String middleName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate bornDate;
    private String phoneNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;

    @Override
    public EventType getEventType() {
        return EventType.USER_PROFILE_UPDATED;
    }

    @Override
    public UUID getMessageId() {
        return messageId;
    }
}

