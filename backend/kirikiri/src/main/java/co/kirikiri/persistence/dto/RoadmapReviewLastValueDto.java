package co.kirikiri.persistence.dto;

import co.kirikiri.service.dto.CustomReviewScrollRequest;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RoadmapReviewLastValueDto {

    private final LocalDateTime lastCreatedAt;
    private final Double lastReviewRate;

    public static RoadmapReviewLastValueDto create(final CustomReviewScrollRequest request) {
        if (request.lastCreatedAt() == null && request.lastReviewRate() == null) {
            return null;
        }
        if (request.lastReviewRate() == null) {
            return new RoadmapReviewLastValueDto(request.lastCreatedAt(), null);
        }
        return new RoadmapReviewLastValueDto(null, request.lastReviewRate());
    }
}
