package uz.consortgroup.notification_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.util.JwtTokenProvider;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordTokenValidator {
    private final JwtTokenProvider jwtTokenProvider;

    public void validateTokensAsync(List<PasswordResetRequestedEvent> messages) {
        CompletableFuture.supplyAsync(() -> {
            log.debug("Starting async token validation for {} messages", messages.size());
            List<Boolean> results = messages.stream()
                    .map(message -> {
                        boolean valid = jwtTokenProvider.validateToken(message.getToken());
                        log.debug("Token for user {} is {}", message.getUserId(), valid ? "valid" : "invalid");
                        return valid;
                    })
                    .collect(Collectors.toList());
            log.debug("Completed async token validation");
            return results;
        });
    }

    public void getExpirationDatesFromTokensAsync(List<PasswordResetRequestedEvent> messages) {
        CompletableFuture.supplyAsync(() -> {
            log.debug("Starting async token expiration extraction for {} messages", messages.size());
            List<Date> expirations = messages.stream()
                    .map(message -> {
                        Date date = jwtTokenProvider.getExpirationDateFromToken(message.getToken());
                        log.debug("Token expiration date for user {}: {}", message.getUserId(), date);
                        return date;
                    })
                    .collect(Collectors.toList());
            log.debug("Completed async token expiration extraction");
            return expirations;
        });
    }
}
