package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.service.dto.member.MemberResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoadmapResponse(
        Long roadmapId,
        RoadmapCategoryResponse category,
        String title,
        String introduction,
        MemberResponse creator,
        String content,
        String difficulty,
        int recommendedRoadmapPeriod,
        List<RoadmapNodeResponse> nodes
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
        this(roadmapId, category, roadmapTitle, introduction, creator, null, difficulty, recommendedRoadmapPeriod,
                null);
    }
}
