package uz.consortgroup.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.consortgroup.notification_service.entity.UserInformation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, UUID> {
    @Query("SELECT u.userId FROM UserInformation u WHERE u.email IN :emails")
    List<UUID> findUserIdsByEmails(List<String> emails);

    @Query("SELECT u FROM UserInformation u WHERE u.userId IN :userIds")
    List<UserInformation> findAllByUserIds(@Param("userIds") List<UUID> userIds);

    @Modifying
    @Query(nativeQuery = true, value = """
    UPDATE notification_schema.user_information
    SET last_name = COALESCE(:lastName, last_name), 
        first_name = COALESCE(:firstName, first_name),
        middle_name = COALESCE(:middleName, middle_name),
        born_date = COALESCE(:bornDate, born_date), 
        phone_number = COALESCE(:phoneNumber, phone_number),    
        updated_at = NOW()
    WHERE user_id = :userId
    RETURNING *""")
    List<UserInformation> updateUserInfoAndReturn(@Param("userId") UUID userId,
                                                  @Param("lastName") String lastName,
                                                  @Param("firstName") String firstName,
                                                  @Param("middleName") String middleName,
                                                  @Param("bornDate") LocalDate bornDate,
                                                  @Param("phoneNumber") String phoneNumber);
}
