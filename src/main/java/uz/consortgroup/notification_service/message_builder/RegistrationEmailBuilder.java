package uz.consortgroup.notification_service.message_builder;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.PersonalizableEmailContent;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RegistrationEmailBuilder implements EmailMessageBuilder {
    private final MessageSource messageSource;

    @Override
    public String buildSubject(EmailContent content, Locale locale) {
        Locale targetLocale = locale != null ? locale : content.getLocale();
        return messageSource.getMessage(
                "email.registration.subject",
                null,
                targetLocale
        );
    }

    @Override
    public String buildBody(EmailContent content, Locale locale) {
        Locale targetLocale = locale != null ? locale : content.getLocale();
        System.out.println("Final locale for body: " + targetLocale);

        if (!(content instanceof PersonalizableEmailContent personalizableContent)) {
            throw new IllegalArgumentException("Registration email requires personalizable content");
        }

        return messageSource.getMessage(
                "email.registration.body",
                new Object[]{
                        personalizableContent.getFirstName(),
                        personalizableContent.getMiddleName(),
                        personalizableContent.getVerificationCode()
                },
                targetLocale
        );
    }
}
