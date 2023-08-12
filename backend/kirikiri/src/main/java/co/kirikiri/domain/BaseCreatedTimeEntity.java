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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        // 초기에 저장된 시간을 가져옵니다.
        LocalDateTime currentTime = createdAt;

        // 초를 6자리까지만 저장하기 위해 포맷을 적용합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        String formattedTime = currentTime.format(formatter);

        // 포맷된 시간을 LocalDateTime으로 다시 파싱하여 createdAt에 할당합니다.
        createdAt = LocalDateTime.parse(formattedTime, formatter);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
