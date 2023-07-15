package co.kirikiri.service.dto.roadmap;

import co.kirikiri.service.dto.member.MemberResponse;
import java.util.List;

public record RoadmapDetailResponse(
        RoadmapCategoryResponse category,
        String title,
        String introduction,
        MemberResponse creator,
        String content,
        String difficulty,
        int recommendedRoadmapPeriod,
        List<RoadmapNodeResponse> nodes
) {

}
