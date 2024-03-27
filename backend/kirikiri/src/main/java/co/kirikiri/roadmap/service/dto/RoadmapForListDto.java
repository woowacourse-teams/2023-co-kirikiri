package co.kirikiri.roadmap.service.dto;

import co.kirikiri.member.service.dto.MemberDto;
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
