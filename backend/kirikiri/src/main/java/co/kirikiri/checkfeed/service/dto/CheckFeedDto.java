package co.kirikiri.checkfeed.service.dto;

import java.time.LocalDateTime;

public record CheckFeedDto(
        Long id,
        String imageUrl,
        String description,
        LocalDateTime createdAt
) {

}
