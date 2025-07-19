package uz.consortgroup.notification_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shaded_package.javax.validation.Valid;
import uz.consortgroup.core.api.v1.dto.user.request.NotificationCreateRequestDto;
import uz.consortgroup.notification_service.service.notification.NotificationTaskService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationTaskController {
    private final NotificationTaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createNotification(@Valid @RequestBody NotificationCreateRequestDto request) {
        taskService.createNotificationTask(request);
    }
}
