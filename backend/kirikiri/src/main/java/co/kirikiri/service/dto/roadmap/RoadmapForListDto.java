package co.kirikiri.service.dto.roadmap;

import co.kirikiri.service.dto.member.MemberDto;
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
