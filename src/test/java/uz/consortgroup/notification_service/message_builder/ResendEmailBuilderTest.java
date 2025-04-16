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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResendEmailBuilderTest {
    @Mock
    private MessageSource messageSource;

    private ResendEmailBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new ResendEmailBuilder(messageSource);
    }

    @Test
    void buildSubject_shouldReturnTranslatedSubject() {
        EmailContent content = mock(EmailContent.class);
        when(messageSource.getMessage("email.resend.subject", null, Locale.ENGLISH))
                .thenReturn("Resend Verification");

        String subject = builder.buildSubject(content, Locale.ENGLISH);

        assertEquals("Resend Verification", subject);
        verify(messageSource).getMessage("email.resend.subject", null, Locale.ENGLISH);
    }

    @Test
    void buildBody_shouldReturnTranslatedBody() {
        EmailContent content = mock(EmailContent.class);
        when(content.getVerificationCode()).thenReturn("5678");
        when(messageSource.getMessage("email.resend.body", new Object[]{"5678"}, Locale.ENGLISH))
                .thenReturn("Your new code is 5678");

        String body = builder.buildBody(content, Locale.ENGLISH);

        assertEquals("Your new code is 5678", body);
        verify(messageSource).getMessage("email.resend.body", new Object[]{"5678"}, Locale.ENGLISH);
    }

    @Test
    void buildSubject_shouldThrowExceptionWhenInvalidContentType() {
        EmailContent invalidContent = new EmailContent() {
            @Override
            public Long getMessageId() {
                return 1L;
            }

            @Override
            public String getEmail() {
                return "test@example.com";
            }

            @Override
            public String getVerificationCode() {
                return "0000";
            }

            @Override
            public EventType getEventType() {
                return EventType.VERIFICATION_CODE_SENT;
            }

            @Override
            public Locale getLocale() {
                return Locale.ENGLISH;
            }
        };

        assertDoesNotThrow(() -> builder.buildSubject(invalidContent, Locale.ENGLISH));
    }

    @Test
    void buildBody_shouldThrowExceptionWhenNullVerificationCode() {
        EmailContent content = mock(EmailContent.class);
        when(content.getVerificationCode()).thenReturn(null);
        when(messageSource.getMessage("email.resend.body", new Object[]{null}, Locale.ENGLISH))
                .thenReturn("Code not found");

        String result = builder.buildBody(content, Locale.ENGLISH);

        assertEquals("Code not found", result);
    }
}
