package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.service.dto.member.response.MemberResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoadmapResponse(
        Long roadmapId,
        RoadmapCategoryResponse category,
        String roadmapTitle,
        String introduction,
        MemberResponse creator,
        RoadmapContentResponse content,
        String difficulty,
        int recommendedRoadmapPeriod,
        LocalDateTime createdAt
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
