package co.kirikiri.service.dto.roadmap;

import co.kirikiri.service.dto.member.MemberResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoadmapResponse(
        Long roadmapId,
        RoadmapCategoryResponse category,
        String roadmapTitle,
        String introduction,
        MemberResponse creator,
        RoadmapContentResponse content,
        String difficulty,
        int recommendedRoadmapPeriod
) {

    public RoadmapResponse(
            final long roadmapId,
            final String roadmapTitle,
            final String introduction,
            final String difficulty,
            final int recommendedRoadmapPeriod,
            final MemberResponse creator,
            final RoadmapCategoryResponse category
    ) {
        this(roadmapId, category, roadmapTitle, introduction, creator, null, difficulty, recommendedRoadmapPeriod);
    }
}
