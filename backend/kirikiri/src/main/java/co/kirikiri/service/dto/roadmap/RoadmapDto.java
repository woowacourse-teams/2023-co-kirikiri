package co.kirikiri.service.dto.roadmap;

import co.kirikiri.service.dto.member.MemberDto;
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
        List<RoadmapTagDto> tags
) {
}
