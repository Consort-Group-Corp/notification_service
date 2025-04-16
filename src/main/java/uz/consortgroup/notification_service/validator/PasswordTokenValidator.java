package uz.consortgroup.notification_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.consortgroup.notification_service.event.PasswordResetRequestedEvent;
import uz.consortgroup.notification_service.util.JwtTokenProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PasswordTokenValidator {
    private final JwtTokenProvider jwtTokenProvider;

    public void validateTokensAsync(List<PasswordResetRequestedEvent> messages) {
        CompletableFuture.supplyAsync(() ->
                messages.stream()
                        .map(message -> jwtTokenProvider.validateToken(message.getToken()))
                        .collect(Collectors.toList())
        );
    }

    public void getExpirationDatesFromTokensAsync(List<PasswordResetRequestedEvent> messages) {
        CompletableFuture.supplyAsync(() ->
                messages.stream()
                        .map(message -> jwtTokenProvider.getExpirationDateFromToken(message.getToken()))
                        .collect(Collectors.toList())
        );
    }
}
