package co.kirikiri.service.dto.roadmap.response;

import java.time.LocalDateTime;

public record MemberRoadmapResponse(
        Long roadmapId,
        String roadmapTitle,
        String difficulty,
        LocalDateTime createdAt,
        RoadmapCategoryResponse category
) {

}
