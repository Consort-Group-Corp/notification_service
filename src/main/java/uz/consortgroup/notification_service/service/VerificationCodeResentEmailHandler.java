package uz.consortgroup.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class VerificationCodeResentEmailHandler implements EmailContentHandler<VerificationCodeResentEvent> {

    private final MessageSource messageSource;

    @Override
    public Class<VerificationCodeResentEvent> getSupportedClass() {
        return VerificationCodeResentEvent.class;
    }

    @Override
    public String getRecipient(VerificationCodeResentEvent event) {
        return event.getEmail();
    }

    @Override
    public String getSubject(VerificationCodeResentEvent event) {
        Locale locale = Locale.forLanguageTag(event.getLanguage().name());
        return messageSource.getMessage("email.resend.subject", null, locale);
    }

    @Override
    public String getBody(VerificationCodeResentEvent event) {
        return messageSource.getMessage("email.resend.body",
                new Object[]{event.getVerificationCode()},
                event.getLocale());
    }
}