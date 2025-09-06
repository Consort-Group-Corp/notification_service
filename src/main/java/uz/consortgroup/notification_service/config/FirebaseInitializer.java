package uz.consortgroup.notification_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "true")
@Configuration
public class FirebaseInitializer {
    @Value("${firebase.credentials:}")
    private String credsPath;

    @PostConstruct
    public void init() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) return;

        InputStream in;
        if (!credsPath.isBlank()) {
            in = java.nio.file.Files.newInputStream(java.nio.file.Path.of(credsPath));
        } else {
            org.springframework.core.io.ClassPathResource cp =
                    new org.springframework.core.io.ClassPathResource(
                            "firebase/consort-group-corp-firebase-adminsdk-fbsvc-54e3a0624f.json");
            if (!cp.exists()) {
                throw new IllegalStateException("No Firebase credentials: set firebase.credentials or place file in classpath");
            }
            in = cp.getInputStream();
        }

        try (in) {
            FirebaseOptions opts = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(in))
                    .build();
            FirebaseApp.initializeApp(opts);
        }
    }
}

