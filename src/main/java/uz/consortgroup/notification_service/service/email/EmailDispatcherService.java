package uz.consortgroup.notification_service.service.email;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDispatcherService {

    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;
    private final TaskExecutor async;
    private final EmailDispatchProperties properties;

    private Semaphore semaphore;

    @PostConstruct
    private void initSemaphore() {
        this.semaphore = new Semaphore(properties.getMaxConcurrentEmails());
        log.info("Initialized semaphore with maxConcurrentEmails={}", properties.getMaxConcurrentEmails());
    }

    public void dispatch(List<EmailContent> contents, Locale locale) {
        log.info("Dispatching {} emails with locale {}", contents.size(), locale);
        dispatch(contents, locale, properties.getChunkSize());
    }

    public void dispatch(List<EmailContent> contents, Locale locale, int chunkSize) {
        log.info("Dispatching emails in chunks: total={}, chunkSize={}", contents.size(), chunkSize);

        List<List<EmailContent>> chunks = Lists.partition(contents, chunkSize);
        for (List<EmailContent> chunk : chunks) {
            log.debug("Processing chunk of size {}", chunk.size());

            List<CompletableFuture<Void>> futures = new ArrayList<>(chunk.size());

            for (EmailContent content : chunk) {
                UUID messageId = content.getMessageId();

                if (!markIfNotProcessed(messageId)) {
                    log.info("Skipping duplicate email with messageId={}", messageId);
                    continue;
                }

                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        semaphore.acquire();
                        log.debug("Sending email with messageId={} (acquired semaphore)", messageId);
                        sendEmailWithRetry(content, locale);
                        log.info("Successfully sent email with messageId={}", messageId);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("Thread interrupted while sending email with messageId={}", messageId, e);
                        throw new EmailDispatchException("Thread interrupted while sending email", e);
                    } catch (Exception e) {
                        log.error("Unexpected error during email dispatch for messageId={}", messageId, e);
                        throw new CompletionException(e);
                    } finally {
                        semaphore.release();
                        log.debug("Released semaphore for messageId={}", messageId);
                    }
                }, async);

                futures.add(future);
            }

            try {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                log.info("Finished processing chunk of size {}", chunk.size());
            } catch (CompletionException e) {
                log.error("Chunk processing failed", e);
                throw new EmailDispatchException("Error sending email", e);
            }
        }
    }

    private boolean markIfNotProcessed(UUID messageId) {
        String key = "event_processed:" + messageId;
        Boolean wasSet = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", Duration.ofMinutes(1));
        log.debug("Marking messageId={} as processed: wasSet={}", messageId, wasSet);
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
                log.warn("Retry {}/3 failed for messageId={}", 3 - retries, content.getMessageId(), e);
                if (retries == 0) {
                    log.error("All retries failed for messageId={}", content.getMessageId(), e);
                    throw e;
                }
            }
        }
    }
}
