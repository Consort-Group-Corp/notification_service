package uz.consortgroup.notification_service.message_builder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import uz.consortgroup.notification_service.event.EmailContent;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetEmailBuilderTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private EmailContent emailContent;

    @InjectMocks
    private PasswordResetEmailBuilder passwordResetEmailBuilder;

    @Test
    void getContentType_shouldReturnEmailContentClass() {
        assertEquals(EmailContent.class, passwordResetEmailBuilder.getContentType());
    }

    @Test
    void getSubjectKey_shouldReturnCorrectKey() {
        assertEquals("email.password-reset.subject", passwordResetEmailBuilder.getSubjectKey());
    }

    @Test
    void getBodyKey_shouldReturnCorrectKey() {
        assertEquals("email.password-reset.body", passwordResetEmailBuilder.getBodyKey());
    }

    @Test
    void getBodyArgs_shouldReturnResetLinkWhenPresent() {
        String testLink = "https://example.com/reset";
        when(emailContent.getResetLink()).thenReturn(testLink);

        Object[] args = passwordResetEmailBuilder.getBodyArgs(emailContent);

        assertEquals(1, args.length);
        assertEquals(testLink, args[0]);
    }

    @Test
    void getBodyArgs_shouldReturnNullWhenNoResetLink() {
        when(emailContent.getResetLink()).thenReturn(null);

        Object[] args = passwordResetEmailBuilder.getBodyArgs(emailContent);

        assertEquals(1, args.length);
        assertEquals(null, args[0]);
    }

    @Test
    void buildBody_shouldReturnTranslatedBodyWithResetLink() {
        String testLink = "https://example.com/reset";
        when(emailContent.getResetLink()).thenReturn(testLink);
        when(messageSource.getMessage(
                eq("email.password-reset.body"),
                eq(new Object[]{testLink}),
                eq(Locale.ENGLISH)
        )).thenReturn("Reset link: " + testLink);

        String body = passwordResetEmailBuilder.buildBody(emailContent, Locale.ENGLISH);

        assertEquals("Reset link: " + testLink, body);
    }

    @Test
    void buildBody_shouldHandleNullResetLink() {
        when(emailContent.getResetLink()).thenReturn(null);
        when(messageSource.getMessage(
                eq("email.password-reset.body"),
                eq(new Object[]{null}),
                eq(Locale.ENGLISH)
        )).thenReturn("Invalid reset link");

        String body = passwordResetEmailBuilder.buildBody(emailContent, Locale.ENGLISH);

        assertEquals("Invalid reset link", body);
    }
}