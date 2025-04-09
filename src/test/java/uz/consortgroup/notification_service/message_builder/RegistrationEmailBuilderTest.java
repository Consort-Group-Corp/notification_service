package uz.consortgroup.notification_service.message_builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.event.PersonalizableEmailContent;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationEmailBuilderTest {

    @Mock
    private MessageSource messageSource;

    private RegistrationEmailBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new RegistrationEmailBuilder(messageSource);
    }

    @Test
    void buildSubject_shouldReturnTranslatedSubject() {
        when(messageSource.getMessage(eq("email.registration.subject"), isNull(), eq(Locale.ENGLISH)))
                .thenReturn("Welcome to the platform");

        PersonalizableEmailContent content = mock(PersonalizableEmailContent.class);

        String subject = builder.buildSubject(content, Locale.ENGLISH);

        assertEquals("Welcome to the platform", subject);
        verify(messageSource).getMessage("email.registration.subject", null, Locale.ENGLISH);
    }

    @Test
    void buildBody_shouldReturnTranslatedBody() {
        PersonalizableEmailContent content = mock(PersonalizableEmailContent.class);
        when(content.getFirstName()).thenReturn("John");
        when(content.getMiddleName()).thenReturn("Doe");
        when(content.getVerificationCode()).thenReturn("1234");

        when(messageSource.getMessage(eq("email.registration.body"), eq(new Object[]{"John", "Doe", "1234"}), eq(Locale.ENGLISH)))
                .thenReturn("Dear John Doe, your verification code is 1234");

        String body = builder.buildBody(content, Locale.ENGLISH);

        assertEquals("Dear John Doe, your verification code is 1234", body);
        verify(messageSource).getMessage("email.registration.body", new Object[]{"John", "Doe", "1234"}, Locale.ENGLISH);
    }

    @Test
    void buildSubject_shouldThrowExceptionWhenInvalidContentType() {
       EmailContent emailContent = new EmailContent() {
           @Override
           public Long getMessageId() {
               return -1L;
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
           public Locale getLocale() {
               return Locale.ENGLISH;
           }
       };

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                builder.buildBody(emailContent, Locale.ENGLISH));

        assertEquals("Invalid content type. Expected: PersonalizableEmailContent", exception.getMessage());
        assertTrue(exception.getMessage().contains("Invalid content type"));
    }

    @Test
    void buildBody_shouldThrowExceptionWhenInvalidContentType() {
        EmailContent emailContent = new EmailContent() {
            @Override
            public Long getMessageId() {
                return -1L;
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
            public Locale getLocale() {
                return Locale.ENGLISH;
            }
        };
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                builder.buildBody(emailContent, Locale.ENGLISH));

        assertEquals("Invalid content type. Expected: PersonalizableEmailContent", exception.getMessage());
    }
}

