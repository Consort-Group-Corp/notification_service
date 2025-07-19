package uz.consortgroup.notification_service.service.email;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import uz.consortgroup.notification_service.asspect.annotation.AspectAfterThrowing;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.config.properties.EmailDispatchProperties;
import uz.consortgroup.notification_service.event.EmailContent;
import uz.consortgroup.notification_service.exception.EmailDispatchException;
import uz.consortgroup.notification_service.exception.EmailSendingException;

import jakarta.annotation.PostConstruct;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Semaphore;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDispatcherService {
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;
    private final TaskExecutor async;
    private final EmailDispatchProperties properties;

    private Semaphore semaphore;

    @PostConstruct
    private void initSemaphore() {
        this.semaphore = new Semaphore(properties.getMaxConcurrentEmails());
    }

    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @AspectAfterThrowing
    public void dispatch(List<EmailContent> contents, Locale locale) {
        dispatch(contents, locale, properties.getChunkSize());
    }

    public void dispatch(List<EmailContent> contents, Locale locale, int chunkSize) {
        List<List<EmailContent>> chunks = Lists.partition(contents, chunkSize);
        for (List<EmailContent> chunk : chunks) {

            List<CompletableFuture<Void>> futures = new ArrayList<>(chunk.size());

            for (EmailContent content : chunk) {
                UUID messageId = content.getMessageId();

                if (!markIfNotProcessed(messageId)) {
                    continue;
                }

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        semaphore.acquire();
                        sendEmailWithRetry(content, locale);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new EmailDispatchException("Thread interrupted while sending email", e);

                    } catch (Exception e) {
                        throw new CompletionException(e);
                    } finally {
                        semaphore.release();
                    }
                }, async);

                futures.add(future);
            }

            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            } catch (CompletionException e) {
                throw new EmailDispatchException("Error sending email", e);
            }
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofMinutes(1));
        return Boolean.TRUE.equals(wasSet);
    }

    private void sendEmailWithRetry(EmailContent content, Locale locale) {
        int retries = 3;
        while (retries > 0) {
            try {
                emailService.sendEmail(content, locale);
                return;
            } catch (EmailSendingException e) {
                retries--;
                log.error("Retrying sending email (attempt {}): {}", 3 - retries, content.getMessageId(), e);
                if (retries == 0) {
                    throw e;
                }
            }
        }
    }
}
