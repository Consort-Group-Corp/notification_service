    package uz.consortgroup.notification_service.entity;

    import jakarta.persistence.CascadeType;
    import jakarta.persistence.Column;
    import jakarta.persistence.Entity;
    import jakarta.persistence.EnumType;
    import jakarta.persistence.Enumerated;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.Id;
    import jakarta.persistence.OneToMany;
    import jakarta.persistence.PrePersist;
    import jakarta.persistence.Table;
    import jakarta.persistence.Version;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.EqualsAndHashCode;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import uz.consortgroup.notification_service.entity.enumeration.Language;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.UUID;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @Builder
    @Entity
    @Table(name = "user_information", schema = "notification_schema")
    public class UserInformation {
        @Id
        @GeneratedValue
        @Column(name = "id", columnDefinition = "uuid")
        private UUID id;

        @Column(name = "user_id", nullable = false, unique = true)
        private UUID userId;

        @Enumerated(EnumType.STRING)
        @Column(name = "language")
        private Language language;

        @Column(name = "last_name")
        private String lastName;

        @Column(name = "first_name")
        private String firstName;

        @Column(name = "middle_name")
        private String middleName;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Column(name = "born_date")
        private LocalDate bornDate;

        @Column(name = "email", nullable = false)
        private String email;

        @OneToMany(mappedBy = "userInformation", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        private List<Notification> notifications;

        @OneToMany(mappedBy = "userInformation", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        private List<UserProfileUpdateLog> userProfileUpdateLogs;

        @Column(name = "created_at")
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @Version
        @Column(name = "version", nullable = false)
        private Integer version;

        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
        }
    }
