package uz.consortgroup.notification_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "notification.email")
public class EmailDispatchProperties {
    private int chunkSize;
    private int maxConcurrentEmails;
}
