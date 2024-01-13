package co.kirikiri.roadmap.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RoadmapForListResponse(
        long roadmapId,
        String roadmapTitle,
        String introduction,
        String difficulty,
        int recommendedRoadmapPeriod,
        LocalDateTime createdAt,
        MemberResponse creator,
        RoadmapCategoryResponse category,
        List<RoadmapTagResponse> tags
) {

}
