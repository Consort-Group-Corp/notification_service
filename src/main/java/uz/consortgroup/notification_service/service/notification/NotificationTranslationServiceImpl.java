package uz.consortgroup.notification_service.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.user.request.TranslationDto;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskTranslation;
import uz.consortgroup.notification_service.repository.NotificationTaskTranslationRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTranslationServiceImpl implements NotificationTranslationService {

    private final NotificationTaskTranslationRepository translationRepository;

    @Override
    @Transactional
    public void saveAll(NotificationTask task, Map<Language, TranslationDto> translations) {
        log.info("Saving translations for taskId={}, languages={}", task.getId(), translations.keySet());

        List<NotificationTaskTranslation> entities = translations.entrySet().stream()
                .map(entry -> NotificationTaskTranslation.builder()
                        .task(task)
                        .language(entry.getKey())
                        .title(entry.getValue().getTitle())
                        .message(entry.getValue().getMessage())
                        .build())
                .toList();

        translationRepository.saveAll(entities);
        log.info("Saved {} translations for taskId={}", entities.size(), task.getId());
    }

    @Override
    public NotificationTaskTranslation getTranslation(NotificationTask task, Language language) {
        log.debug("Fetching translation for taskId={}, language={}", task.getId(), language);
        return translationRepository
                .findByTaskAndLanguage(task, language)
                .orElseThrow(() -> {
                    log.error("Translation not found for taskId={} and language={}", task.getId(), language);
                    return new RuntimeException("Translation not found");
                });
    }
}
