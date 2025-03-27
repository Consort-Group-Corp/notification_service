package uz.consortgroup.notification_service.deserializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class MessageDeserializer <T> extends JsonDeserializer<T> {
    public MessageDeserializer() {
        super();
        this.addTrustedPackages("*");
    }

    private static String getMessage(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.error("Something went wrong when deserializing message {}", new String(data, StandardCharsets.UTF_8), e);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.error("Something went wrong when deserializing message {}", new String(data, StandardCharsets.UTF_8), e);
            return null;
        }
    }
}
