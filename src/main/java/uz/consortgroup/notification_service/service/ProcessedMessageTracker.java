package uz.consortgroup.notification_service.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProcessedMessageTracker {
    private final Set<Long> processedIds = ConcurrentHashMap.newKeySet();

    public boolean isAlreadyProcessed(Long messageId) {
        return processedIds.contains(messageId);
    }

    public void markAsProcessed(Long messageId) {
        processedIds.add(messageId);
    }

    public void clearProcessed(Long messageId) {
        processedIds.remove(messageId);
    }
}