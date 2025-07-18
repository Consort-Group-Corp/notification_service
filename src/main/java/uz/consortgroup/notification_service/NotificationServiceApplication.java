package uz.consortgroup.notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import uz.consortgroup.notification_service.config.properties.EmailDispatchProperties;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableConfigurationProperties(EmailDispatchProperties.class)
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
