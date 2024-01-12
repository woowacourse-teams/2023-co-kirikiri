package co.kirikiri.roadmap.service.dto;

import co.kirikiri.roadmap.service.dto.request.RoadmapDifficultyType;

import java.util.List;

public record RoadmapSaveDto(
        Long categoryId,
        String title,
        String introduction,
        String content,
        RoadmapDifficultyType difficulty,
        Integer requiredPeriod,
        List<RoadmapNodeSaveDto> roadmapNodes,
        List<RoadmapTagSaveDto> tags
) {

}
