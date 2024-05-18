package co.kirikiri.roadmap.service.dto;

import java.util.List;

public record RoadmapForListScrollDto(
        List<RoadmapForListDto> dtos,
        boolean hasNext
) {

}
