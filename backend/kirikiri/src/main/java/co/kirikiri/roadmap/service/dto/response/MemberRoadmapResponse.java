package co.kirikiri.roadmap.service.dto.response;

import java.time.LocalDateTime;

public record MemberRoadmapResponse(
        Long roadmapId,
        String roadmapTitle,
        String difficulty,
        LocalDateTime createdAt,
        RoadmapCategoryResponse category
) {

}
