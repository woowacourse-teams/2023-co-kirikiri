package co.kirikiri.service.dto.roadmap;

import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import java.util.List;

public record RoadmapSaveDto(
        Long categoryId,
        String title,
        String introduction,
        String content,
        RoadmapDifficulty difficulty,
        int requiredPeriod,
        List<RoadmapNodesSaveDto> roadmapNodes
) {

}
