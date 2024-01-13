package co.kirikiri.roadmap.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RoadmapDto(
        Long roadmapId,
        RoadmapCategoryDto category,
        String roadmapTitle,
        String introduction,
        MemberDto creator,
        RoadmapContentDto content,
        String difficulty,
        int recommendedRoadmapPeriod,
        LocalDateTime createdAt,
        List<RoadmapTagDto> tags
) {

}
