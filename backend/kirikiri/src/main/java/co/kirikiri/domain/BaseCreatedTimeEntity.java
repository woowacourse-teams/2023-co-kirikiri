package co.kirikiri.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseCreatedTimeEntity extends BaseEntity {

    static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";

    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        // 초를 6자리까지만 저장하기 위해 포맷을 적용합니다.
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        final String formattedTime = createdAt.format(formatter);

        createdAt = LocalDateTime.parse(formattedTime, formatter);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}