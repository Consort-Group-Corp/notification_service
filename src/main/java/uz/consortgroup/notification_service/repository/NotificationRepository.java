package uz.consortgroup.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.consortgroup.notification_service.entity.Notification;
import uz.consortgroup.notification_service.entity.enumeration.NotificationStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    @Modifying
    @Query("UPDATE Notification n SET n.notificationStatus = :status WHERE n.userInformation.userId IN :userIds")
    void updateStatusForUserIds(@Param("status") NotificationStatus status,
                                @Param("userIds") List<UUID> userIds);
}
