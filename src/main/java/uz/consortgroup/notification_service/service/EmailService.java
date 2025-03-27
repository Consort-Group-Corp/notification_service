package uz.consortgroup.notification_service.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import uz.consortgroup.notification_service.dto.KafkaDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    @Value("${mail.username}")
    private String mailTo;

    @Value("${mail.subjectForEmail}")
    private String subjectForEmail;

    private final JavaMailSender javaMailSender;

    public void sendMail(KafkaDto kafkaDto) {

    }
}
