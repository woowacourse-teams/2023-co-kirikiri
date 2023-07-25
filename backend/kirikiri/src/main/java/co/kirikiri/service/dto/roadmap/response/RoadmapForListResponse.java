package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.service.dto.member.response.MemberResponse;
import java.util.List;

public record RoadmapForListResponse(
        long roadmapId,
        String roadmapTitle,
        String introduction,
        String difficulty,
        int recommendedRoadmapPeriod,
        MemberResponse creator,
        RoadmapCategoryResponse category,
        List<RoadmapTagResponse> tags
) {

}
