package uz.consortgroup.notification_service.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.auth.http.HttpCredentialsAdapter;

import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "mail.provider", havingValue = "gmail-api")
public class GmailConfig {

    @Bean
    public Gmail gmail(
            @Value("${mail.gmail.client-id}") String clientId,
            @Value("${mail.gmail.client-secret}") String clientSecret,
            @Value("${mail.gmail.refresh-token}") String refreshToken
    ) throws Exception {
        var creds = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(creds)
        ).setApplicationName("Consort Notification Service").build();
    }
}
