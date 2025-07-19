package uz.consortgroup.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.core.api.v1.dto.user.enumeration.NotificationStatus;
import uz.consortgroup.notification_service.entity.NotificationLog;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {
    @Modifying
    @Query("UPDATE NotificationLog n SET n.notificationStatus = :status WHERE n.userInformation.userId IN :userIds")
    void updateStatusForUserIds(@Param("status") NotificationStatus status,
                                @Param("userIds") List<UUID> userIds);
}
