package co.kirikiri.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
public class BaseCreatedTimeEntity extends BaseEntity {

    protected static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";

    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @PrePersist
    protected void prePersist() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        final String formattedTime = LocalDateTime.now().format(formatter);
        createdAt = LocalDateTime.parse(formattedTime, formatter);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
