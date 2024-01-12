package co.kirikiri.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
public class BaseUpdatedTimeEntity extends BaseCreatedTimeEntity {

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Override
    protected void prePersist() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        final String formattedTime = LocalDateTime.now().format(formatter);
        createdAt = LocalDateTime.parse(formattedTime, formatter);
        updatedAt = LocalDateTime.parse(formattedTime, formatter);
    }

    @PreUpdate
    private void preUpdate() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        final String formattedTime = LocalDateTime.now().format(formatter);
        updatedAt = LocalDateTime.parse(formattedTime, formatter);
    }
}
