package uz.consortgroup.notification_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uz.consortgroup.notification_service.dto.VerificationKafkaDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    @Value("${mail.subjectForEmail}")
    private String subjectForEmail;

    private final JavaMailSender mailSender;

    public void sendMail(VerificationKafkaDto verificationKafkaDto) {
        try {
            String emailBody = String.format(
                    "Здравствуйте, %s %s!\nВаш код подтверждения: %s",
                    verificationKafkaDto.getFirstName(),
                    verificationKafkaDto.getMiddleName(),
                    verificationKafkaDto.getVerificationCode()
            );

            String recipientEmail = verificationKafkaDto.getEmail();

            log.info("Sending email: {}, to {}", subjectForEmail, recipientEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(recipientEmail);
            helper.setSubject(subjectForEmail);
            helper.setText(emailBody, false);

            log.info("Email message prepared for: {}", recipientEmail);
            mailSender.send(message);
            log.info("Email successfully sent to: {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("Error sending email to {}: {}", verificationKafkaDto.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
