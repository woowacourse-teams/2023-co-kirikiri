package co.kirikiri.service.dto.roadmap;

import java.util.List;

public record RoadmapSaveDto(
        Long categoryId,
        String title,
        String introduction,
        String content,
        RoadmapDifficultyType difficulty,
        Integer requiredPeriod,
        List<RoadmapNodeSaveDto> roadmapNodes
) {

}
