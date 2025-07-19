package uz.consortgroup.notification_service.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.user.request.TranslationDto;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectAfterMethod;
import uz.consortgroup.notification_service.asspect.annotation.LoggingAspectBeforeMethod;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskTranslation;
import uz.consortgroup.notification_service.repository.NotificationTaskTranslationRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationTranslationServiceImpl implements NotificationTranslationService {

    private final NotificationTaskTranslationRepository translationRepository;

    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    @Transactional
    public void saveAll(NotificationTask task, Map<Language, TranslationDto> translations) {
        List<NotificationTaskTranslation> entities = translations.entrySet().stream()
                .map(entry -> NotificationTaskTranslation.builder()
                        .task(task)
                        .language(entry.getKey())
                        .title(entry.getValue().getTitle())
                        .message(entry.getValue().getMessage())
                        .build())
                .toList();
        translationRepository.saveAll(entities);
    }

    @Override
    @LoggingAspectBeforeMethod
    @LoggingAspectAfterMethod
    public NotificationTaskTranslation getTranslation(NotificationTask task, Language language) {
        return translationRepository
                .findByTaskAndLanguage(task, language)
                .orElseThrow(() -> new RuntimeException("Translation not found"));
    }
}
