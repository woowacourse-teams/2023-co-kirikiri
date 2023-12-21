package co.kirikiri.goalroom.service.dto.response;

import java.time.LocalDate;

public record DashBoardCheckFeedResponse(
        Long id,
        String imageUrl,
        String description,
        LocalDate createdAt
) {

}
