package uz.consortgroup.notification_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.notification_service.entity.NotificationTaskRecipient;

import java.util.UUID;

@Repository
public interface NotificationTaskRecipientRepository extends JpaRepository<NotificationTaskRecipient, UUID> {
    Page<NotificationTaskRecipient> findAllByTaskId(UUID taskId, Pageable pageable);
}
