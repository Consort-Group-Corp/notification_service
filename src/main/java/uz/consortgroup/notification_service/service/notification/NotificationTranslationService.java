package uz.consortgroup.notification_service.service.notification;

import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.user.request.TranslationDto;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskTranslation;

import java.util.Map;

public interface NotificationTranslationService {
    void saveAll(NotificationTask task, Map<Language, TranslationDto> translations);
    NotificationTaskTranslation getTranslation(NotificationTask task, Language language);
}
