package uz.consortgroup.notification_service.message_builder;

import uz.consortgroup.notification_service.event.EmailContent;

import java.util.Locale;

public interface EmailMessageBuilder {
    String buildSubject(EmailContent content, Locale locale);
    String buildBody(EmailContent content, Locale locale);
}
