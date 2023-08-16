package co.kirikiri.service.dto.roadmap;

import java.util.List;

public record RoadmapContentDto(
        Long id,
        String content,
        List<RoadmapNodeDto> nodes
) {
}
