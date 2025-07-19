package uz.consortgroup.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;
import uz.consortgroup.notification_service.entity.NotificationTask;
import uz.consortgroup.notification_service.entity.NotificationTaskTranslation;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationTaskTranslationRepository extends JpaRepository<NotificationTaskTranslation, UUID> {
    Optional<NotificationTaskTranslation> findByTaskAndLanguage(NotificationTask task, Language language);
}
