package uz.consortgroup.notification_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import uz.consortgroup.core.api.v1.dto.user.response.FcmTokenDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserDeviceTokenClient {

    @GetMapping("/api/v1/device-tokens/all")
    Page<FcmTokenDto> getAllActiveTokens(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "100") int size);

    @PostMapping("/api/v1/device-tokens/by-user-ids")
    Map<UUID, List<FcmTokenDto>> getTokensByUserIds(@RequestBody List<UUID> userIds);

}
