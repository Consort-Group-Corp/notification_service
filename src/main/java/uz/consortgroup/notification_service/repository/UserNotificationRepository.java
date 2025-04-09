package uz.consortgroup.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.notification_service.entity.NotificationStatus;
import uz.consortgroup.notification_service.entity.UserNotification;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE UserNotification u SET u.notificationStatus = :status WHERE u.email = :email")
    void updateNotificationStatus(@Param("email") String email, @Param("status") NotificationStatus status);

    @Modifying
    @Query("UPDATE UserNotification u SET u.notificationStatus = :status WHERE u.email IN :emails")
    void updateNotificationStatuses(@Param("emails") List<String> emails, @Param("status") NotificationStatus status);
}
