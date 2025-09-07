package uz.consortgroup.notification_service.service.email;

public interface MailSenderPort {
    void send(String to, String subject, String body) throws Exception;
}