package uz.consortgroup.notification_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.consortgroup.core.api.v1.dto.user.response.UserLanguageInfoDto;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-language-client", url = "${user.service.url}")
public interface UserLanguageClient {

    @PostMapping("/api/v1/internal/users/basic-info")
    List<UserLanguageInfoDto> getUserLanguages(@RequestBody List<UUID> userIds);
}
