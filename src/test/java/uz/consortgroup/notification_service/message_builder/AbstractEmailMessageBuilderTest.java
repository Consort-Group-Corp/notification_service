package uz.consortgroup.notification_service.message_builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import uz.consortgroup.notification_service.entity.enumeration.EventType;
import uz.consortgroup.notification_service.event.EmailContent;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AbstractEmailMessageBuilderTest {
    @Mock
    private MessageSource messageSource;
    private TestEmailMessageBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new TestEmailMessageBuilder(messageSource);
    }

    @Test
    void buildSubject_shouldReturnTranslatedSubject() {
        TestEmailContent content = new TestEmailContent(Locale.ENGLISH);
        when(messageSource.getMessage(eq("email.subject"), any(), eq(Locale.ENGLISH)))
                .thenReturn("Subject Text");

        String subject = builder.buildSubject(content, Locale.ENGLISH);

        assertEquals("Subject Text", subject);
        verify(messageSource).getMessage("email.subject", null, Locale.ENGLISH);
    }

    @Test
    void buildBody_shouldReturnTranslatedBody() {
        TestEmailContent content = new TestEmailContent(Locale.ENGLISH);
        when(messageSource.getMessage(eq("email.body"), any(), eq(Locale.ENGLISH)))
                .thenReturn("Body Text");

        String body = builder.buildBody(content, Locale.ENGLISH);

        assertEquals("Body Text", body);
        verify(messageSource).getMessage(eq("email.body"), any(), eq(Locale.ENGLISH));
    }

    @Test
    void checkAndCast_shouldThrowExceptionForInvalidType() {
        EmailContent wrongContent = mock(EmailContent.class); // не TestEmailContent

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            builder.buildBody(wrongContent, Locale.ENGLISH);
        });

        assertTrue(exception.getMessage().contains("Invalid content type"));
    }

    static class TestEmailContent implements EmailContent {
        private final Locale locale;

        public TestEmailContent(Locale locale) {
            this.locale = locale;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        @Override
        public UUID getMessageId() {
            return UUID.randomUUID();
        }

        @Override
        public String getEmail() {
            return "test@gmail.com";
        }

        @Override
        public String getVerificationCode() {
            return "1234";
        }

        @Override
        public EventType getEventType() {
            return EventType.USER_REGISTERED;
        }
    }

    static class TestEmailMessageBuilder extends AbstractEmailMessageBuilder<TestEmailContent> {
        public TestEmailMessageBuilder(MessageSource messageSource) {
            super(messageSource);
        }

        @Override
        protected Class<TestEmailContent> getContentType() {
            return TestEmailContent.class;
        }

        @Override
        protected String getSubjectKey() {
            return "email.subject";
        }

        @Override
        protected String getBodyKey() {
            return "email.body";
        }

        @Override
        protected Object[] getBodyArgs(TestEmailContent content) {
            return new Object[]{"arg1", "arg2"};
        }
    }
}
