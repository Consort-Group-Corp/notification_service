package uz.consortgroup.notification_service.message_builder;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.PersonalizableEmailContent;

@Component
public class RegistrationEmailBuilder extends AbstractEmailMessageBuilder<PersonalizableEmailContent> {

    public RegistrationEmailBuilder(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected Class<PersonalizableEmailContent> getContentType() {
        return PersonalizableEmailContent.class;
    }

    @Override
    protected String getSubjectKey() {
        return "email.registration.subject";
    }

    @Override
    protected String getBodyKey() {
        return "email.registration.body";
    }

    @Override
    protected Object[] getBodyArgs(PersonalizableEmailContent content) {
        return new Object[]{
                content.getFirstName(),
                content.getMiddleName(),
                content.getVerificationCode()
        };
    }
}
