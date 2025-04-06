package uz.consortgroup.notification_service.event;

import java.util.Locale;

public interface EmailContent {
    Long getMessageId();
    String getEmail();
    String getVerificationCode();
    Locale getLocale();
}
