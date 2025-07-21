package uz.consortgroup.notification_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Communication;
import uz.consortgroup.core.api.v1.dto.user.enumeration.CreatorRole;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.user.request.NotificationCreateRequestDto;
import uz.consortgroup.core.api.v1.dto.user.request.TranslationDto;
import uz.consortgroup.notification_service.service.notification.NotificationTaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationTaskController.class)
class NotificationTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationTaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createNotification_shouldReturn201_whenRequestIsValid() throws Exception {
        NotificationCreateRequestDto request = NotificationCreateRequestDto.builder()
                .createdByUserId(UUID.randomUUID())
                .creatorRole(CreatorRole.SUPER_ADMIN)
                .communication(Communication.PUSH)
                .sendAt(LocalDateTime.now().plusMinutes(5))
                .active(true)
                .recipientUserIds(List.of(UUID.randomUUID()))
                .translations(Map.of(
                        Language.RUSSIAN, TranslationDto.builder()
                                .title("Заголовок")
                                .message("Сообщение")
                                .build()
                ))
                .build();

        doNothing().when(taskService).createNotificationTask(request);

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createNotification_shouldReturn400_whenRequestIsInvalid() throws Exception {
        NotificationCreateRequestDto invalidRequest = NotificationCreateRequestDto.builder()
                .createdByUserId(null)
                .creatorRole(null)
                .communication(Communication.PUSH)
                .sendAt(LocalDateTime.now().minusHours(1))
                .active(true)
                .recipientUserIds(List.of())
                .translations(Map.of())
                .build();

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
