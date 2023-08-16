package co.kirikiri.service.dto.goalroom;

import java.time.LocalDateTime;

public record CheckFeedDto(
        Long id,
        String imageUrl,
        String description,
        LocalDateTime createdAt
) {

}
