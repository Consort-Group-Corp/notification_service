package uz.consortgroup.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.consortgroup.notification_service.event.ResentVerificationEmailContent;
import uz.consortgroup.notification_service.event.UserRegistrationEmailContent;
import uz.consortgroup.notification_service.event.UserRegistrationEvent;
import uz.consortgroup.notification_service.event.VerificationCodeResentEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private static final String EMAIL_GREETING = "Здравствуйте, %s %s!\nВаш %s код подтверждения: %s";
    private static final String EMAIL_RESEND_MESSAGE = "Ваш повторный код подтверждения: %s";
    private static final String LOG_EMAIL_PREPARED = "Email message prepared for: {}";
    private static final String LOG_EMAIL_SENT = "Email successfully sent to: {}";
    private static final String LOG_EMAIL_ERROR = "Error sending email to {}: {}";
    private static final String CHARSET_UTF8 = "UTF-8";

    @Value("${mail.subjectForEmail}")
    private String subjectForEmail;

    private final JavaMailSender mailSender;

    public void sendEmail(UserRegistrationEmailContent userRegistrationEmailContent,
                          ResentVerificationEmailContent resentVerificationEmailContent) {
        try {
            String emailBody = buildEmailBody(userRegistrationEmailContent, resentVerificationEmailContent);

            String logAction = userRegistrationEmailContent
                    != null && userRegistrationEmailContent.isResend() ? "Resending" : "Sending";

            log.info("{} email: {}, to {}", logAction, subjectForEmail,
                    userRegistrationEmailContent != null ? userRegistrationEmailContent.getEmail() : resentVerificationEmailContent.getEmail());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, CHARSET_UTF8);
            helper.setTo(userRegistrationEmailContent != null ?
                    userRegistrationEmailContent.getEmail() : resentVerificationEmailContent.getEmail());
            helper.setSubject(subjectForEmail);
            helper.setText(emailBody, false);

            log.info(LOG_EMAIL_PREPARED, userRegistrationEmailContent != null ?
                    userRegistrationEmailContent.getEmail() : resentVerificationEmailContent.getEmail());
            mailSender.send(message);

            log.info(LOG_EMAIL_SENT, userRegistrationEmailContent != null ?
                    userRegistrationEmailContent.getEmail() : resentVerificationEmailContent.getEmail());

        } catch (MessagingException e) {
            log.error(LOG_EMAIL_ERROR, userRegistrationEmailContent != null ?
                    userRegistrationEmailContent.getEmail() : resentVerificationEmailContent.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private static String buildEmailBody(UserRegistrationEmailContent userRegistrationEmailContent,
                                         ResentVerificationEmailContent resentVerificationEmailContent) {
        String emailBody;
        if (userRegistrationEmailContent != null) {
            String action = userRegistrationEmailContent.isResend() ? "повторный " : "";
            emailBody = String.format(EMAIL_GREETING,
                    userRegistrationEmailContent.getFirstName(),
                    userRegistrationEmailContent.getMiddleName(),
                    action,
                    userRegistrationEmailContent.getVerificationCode());
        } else {
            emailBody = String.format(EMAIL_RESEND_MESSAGE, resentVerificationEmailContent.getVerificationCode());
        }
        return emailBody;
    }

    public void sendMail(UserRegistrationEvent event) {
        sendEmail(event, null);
    }

    public void resendVerificationCode(VerificationCodeResentEvent event) {
        sendEmail(null, event);
    }
}