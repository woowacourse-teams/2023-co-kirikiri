package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.service.dto.member.response.MemberResponse;
import java.util.List;

public record RoadmapResponse(
        Long roadmapId,
        RoadmapCategoryResponse category,
        String roadmapTitle,
        String introduction,
        MemberResponse creator,
        RoadmapContentResponse content,
        String difficulty,
        int recommendedRoadmapPeriod,
        List<RoadmapTagResponse> tags
) {
}
