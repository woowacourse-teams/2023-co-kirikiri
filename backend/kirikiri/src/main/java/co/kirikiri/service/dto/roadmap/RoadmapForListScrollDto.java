package co.kirikiri.service.dto.roadmap;

import java.util.List;

public record RoadmapForListScrollDto(
        List<RoadmapForListDto> dtos,
        boolean hasNext
) {
}
