package uz.consortgroup.notification_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.core.api.v1.dto.user.enumeration.TaskStatus;
import uz.consortgroup.notification_service.entity.NotificationTask;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, UUID> {
    Page<NotificationTask> findAllByActiveTrueAndStatusAndSendAtLessThanEqual(
            TaskStatus status,
            LocalDateTime maxTime,
            Pageable pageable
    );
}
