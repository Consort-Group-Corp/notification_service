package uz.consortgroup.notification_service.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.core.api.v1.dto.user.request.TranslationDto;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskTranslation;
import uz.consortgroup.notification_service.repository.NotificationTaskTranslationRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationTranslationServiceImplTest {

    private NotificationTaskTranslationRepository repository;
    private NotificationTranslationServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(NotificationTaskTranslationRepository.class);
        service = new NotificationTranslationServiceImpl(repository);
    }

    @Test
    void shouldSaveAllTranslations() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();

        TranslationDto ru = TranslationDto.builder().title("Заголовок").message("Сообщение").build();
        TranslationDto uz = TranslationDto.builder().title("Sarlavha").message("Xabar").build();

        Map<Language, TranslationDto> map = Map.of(
                Language.RUSSIAN, ru,
                Language.UZBEK, uz
        );

        service.saveAll(task, map);

        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldReturnTranslationByLanguage() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();

        NotificationTaskTranslation translation = NotificationTaskTranslation.builder()
                .task(task)
                .language(Language.RUSSIAN)
                .title("Заголовок")
                .message("Сообщение")
                .build();

        when(repository.findByTaskAndLanguage(task, Language.RUSSIAN))
                .thenReturn(Optional.of(translation));

        NotificationTaskTranslation result = service.getTranslation(task, Language.RUSSIAN);

        assertThat(result).isEqualTo(translation);
    }

    @Test
    void shouldThrowExceptionIfTranslationNotFound() {
        NotificationTask task = NotificationTask.builder().id(UUID.randomUUID()).build();

        when(repository.findByTaskAndLanguage(task, Language.UZBEK)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.getTranslation(task, Language.UZBEK));

        assertThat(exception.getMessage()).isEqualTo("Translation not found");
    }
}
