package co.kirikiri.roadmap.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RoadmapForListDto(
        long roadmapId,
        String roadmapTitle,
        String introduction,
        String difficulty,
        int recommendedRoadmapPeriod,
        LocalDateTime createdAt,
        MemberDto creator,
        RoadmapCategoryDto category,
        List<RoadmapTagDto> tags
) {

}
