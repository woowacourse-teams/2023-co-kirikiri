package co.kirikiri.service.dto.goalroom.response;

import java.time.LocalDateTime;

public record CheckFeedResponse(
        Long id,
        String imageUrl,
        String description,
        LocalDateTime createdAt
) {

}
