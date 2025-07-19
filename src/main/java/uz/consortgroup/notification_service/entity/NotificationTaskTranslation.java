package uz.consortgroup.notification_service.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.consortgroup.core.api.v1.dto.user.enumeration.Language;

import java.util.UUID;

@Entity
@Table(name = "notification_task_translation", schema = "notification_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTaskTranslation {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private NotificationTask task;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 20)
    private Language language;

    @Column(name = "title", nullable = false, columnDefinition = "text")
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;
}
