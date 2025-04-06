package uz.consortgroup.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class UserRegistrationEmailHandler implements EmailContentHandler<UserRegistrationEvent> {
    private final MessageSource messageSource;

    @Override
    public Class<UserRegistrationEvent> getSupportedClass() {
        return UserRegistrationEvent.class;
    }

    @Override
    public String getRecipient(UserRegistrationEvent event) {
        return event.getEmail();
    }

    @Override
    public String getSubject(UserRegistrationEvent event) {
        Locale locale = event.getLocale();
        return messageSource.getMessage("email.registration.subject", null, locale);
    }

    @Override
    public String getBody(UserRegistrationEvent event) {
        return messageSource.getMessage("email.registration.body",
                new Object[]{
                        event.getFirstName(),
                        event.getMiddleName(),
                        event.getVerificationCode()},
                event.getLocale());
    }
}
