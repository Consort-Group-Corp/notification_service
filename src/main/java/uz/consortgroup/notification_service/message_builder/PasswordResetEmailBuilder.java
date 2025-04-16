package uz.consortgroup.notification_service.message_builder;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.EmailContent;

@Component
public class PasswordResetEmailBuilder extends AbstractEmailMessageBuilder<EmailContent> {
    protected PasswordResetEmailBuilder(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected Class<EmailContent> getContentType() {
        return EmailContent.class;
    }

    @Override
    protected String getSubjectKey() {
        return "email.password-reset.subject";
    }

    @Override
    protected String getBodyKey() {
        return "email.password-reset.body";
    }

    @Override
    protected Object[] getBodyArgs(EmailContent content) {
        return new Object[] {content.getResetLink()};
    }
}
