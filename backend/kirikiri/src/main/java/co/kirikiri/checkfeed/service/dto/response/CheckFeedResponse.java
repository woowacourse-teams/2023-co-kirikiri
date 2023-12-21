package co.kirikiri.checkfeed.service.dto.response;

import java.time.LocalDate;

public record CheckFeedResponse(
        Long id,
        String imageUrl,
        String description,
        LocalDate createdAt
) {

}
