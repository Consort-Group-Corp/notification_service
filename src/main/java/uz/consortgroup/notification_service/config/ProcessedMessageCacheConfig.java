package uz.consortgroup.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ProcessedMessageCacheConfig {
    @Bean
    public Set<Long> processedIds() {
        return ConcurrentHashMap.newKeySet();
    }
}
