package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.member.service.dto.response.MemberResponse;
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
