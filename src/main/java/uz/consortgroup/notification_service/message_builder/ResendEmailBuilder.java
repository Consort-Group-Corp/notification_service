package uz.consortgroup.notification_service.message_builder;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.EmailContent;

@Component
public class ResendEmailBuilder extends AbstractEmailMessageBuilder<EmailContent> {
    public ResendEmailBuilder(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    protected Class<EmailContent> getContentType() {
        return EmailContent.class;
    }

    @Override
    protected String getSubjectKey() {
        return "email.resend.subject";
    }

    @Override
    protected String getBodyKey() {
        return "email.resend.body";
    }

    @Override
    protected Object[] getBodyArgs(EmailContent content) {
        return new Object[] {content.getVerificationCode()};
    }
}