package co.kirikiri.roadmap.service.dto;

import co.kirikiri.member.service.dto.MemberDto;
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
