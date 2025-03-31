package uz.consortgroup.notification_service.kafka;

public interface MessageProcessor<T> {
    void process(T message);
}
