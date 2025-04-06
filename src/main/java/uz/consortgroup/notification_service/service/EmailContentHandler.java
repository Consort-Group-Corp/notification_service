package uz.consortgroup.notification_service.service;

public interface EmailContentHandler<T> {
    Class<T> getSupportedClass();
    String getRecipient(T event);
    String getSubject(T event);
    String getBody(T event);
}