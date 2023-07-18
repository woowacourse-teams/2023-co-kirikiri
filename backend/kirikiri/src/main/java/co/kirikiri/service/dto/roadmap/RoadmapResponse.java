package co.kirikiri.service.dto.roadmap;

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
            long roadmapId,
            String roadmapTitle,
            String introduction,
            String difficulty,
            int recommendedRoadmapPeriod,
            MemberResponse creator,
            RoadmapCategoryResponse category
    ) {
        this(roadmapId, category, roadmapTitle, introduction, creator, null, difficulty, recommendedRoadmapPeriod, null);
    }
}
