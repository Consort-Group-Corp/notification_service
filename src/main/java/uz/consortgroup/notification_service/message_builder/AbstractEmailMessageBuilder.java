package uz.consortgroup.notification_service.message_builder;

import org.springframework.context.MessageSource;
import uz.consortgroup.notification_service.event.EmailContent;

import java.util.Locale;

public abstract class AbstractEmailMessageBuilder<T extends EmailContent> implements EmailMessageBuilder {
    protected final MessageSource messageSource;

    protected AbstractEmailMessageBuilder(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String buildSubject(EmailContent content, Locale locale) {
        return messageSource.getMessage(
                getSubjectKey(),
                null,
                resolveLocale(content, locale)
        );
    }

    @Override
    public String buildBody(EmailContent content, Locale locale) {
        Locale targetLocale = resolveLocale(content, locale);
        T specificContent = checkAndCast(content);
        return messageSource.getMessage(
                getBodyKey(),
                getBodyArgs(specificContent),
                targetLocale
        );
    }

    protected Locale resolveLocale(EmailContent content, Locale locale) {
        return locale != null ? locale : content.getLocale();
    }

    @SuppressWarnings("unchecked")
    protected T checkAndCast(EmailContent content) {
        if (!getContentType().isInstance(content)) {
            throw new IllegalArgumentException("Invalid content type. Expected: " + getContentType().getSimpleName());
        }
        return (T) content;
    }

    protected abstract Class<T> getContentType();

    protected abstract String getSubjectKey();

    protected abstract String getBodyKey();

    protected abstract Object[] getBodyArgs(T content);
}
