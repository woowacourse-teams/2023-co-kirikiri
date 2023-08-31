package co.kirikiri.service.dto.goalroom.response;

import java.time.LocalDate;

public record CheckFeedResponse(
        Long id,
        String imageUrl,
        String description,
        LocalDate createdAt
) {

}
