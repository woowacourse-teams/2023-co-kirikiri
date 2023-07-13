package co.kirikiri.service.dto.roadmap;

import co.kirikiri.service.dto.member.MemberResponse;

public record RoadmapResponse(
    long roadmapId,
    String roadmapTitle,
    String introduction,
    String difficulty,
    int recommendedRoadmapPeriod,
    MemberResponse creator,
    RoadmapCategoryResponse category
) {

}
