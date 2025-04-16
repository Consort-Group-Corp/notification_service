package uz.consortgroup.notification_service.event;

import uz.consortgroup.notification_service.entity.enumeration.EventType;

import java.util.List;

public interface EventProcessor<T extends EmailContent> {
    boolean canHandle(EventType eventType);
    void process(List<T> event);
}