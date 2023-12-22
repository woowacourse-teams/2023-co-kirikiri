package co.kirikiri.roadmap.service.dto;

import java.util.List;

public record RoadmapContentDto(
        Long id,
        String content,
        List<RoadmapNodeDto> nodes
) {

}
