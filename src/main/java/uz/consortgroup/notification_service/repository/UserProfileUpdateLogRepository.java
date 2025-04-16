package uz.consortgroup.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.consortgroup.notification_service.entity.UserProfileUpdateLog;

import java.util.UUID;

@Repository
public interface UserProfileUpdateLogRepository extends JpaRepository<UserProfileUpdateLog, UUID> {
}
