package uz.consortgroup.notification_service.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class ProcessedMessageCacheCleaner {
    private final Set<UUID> processedIds;

    public ProcessedMessageCacheCleaner(Set<UUID> processedIds) {
        this.processedIds = processedIds;
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void clearProcessedIds() {
        log.info("Processed IDs before clear: {}", processedIds);
        processedIds.clear();
        log.info("Processed IDs cache cleared.");
    }
}
