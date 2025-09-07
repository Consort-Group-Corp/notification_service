package uz.consortgroup.notification_service.service.email;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mail.provider", havingValue = "gmail-api")
public class GmailApiMailSender implements MailSenderPort {

    private final Gmail gmail;

    @Value("${mail.from}")
    private String from;

    @Override
    public void send(String to, String subject, String body) throws Exception {
        Session session = Session.getInstance(new Properties(), null);
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(from));
        mimeMessage.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        mimeMessage.setSubject(subject, "UTF-8");
        mimeMessage.setText(body, "UTF-8");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mimeMessage.writeTo(baos);
        var raw = Base64.getUrlEncoder().withoutPadding().encodeToString(baos.toByteArray());

        gmail.users().messages().send("me", new Message().setRaw(raw)).execute();
    }
}
