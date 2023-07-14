package co.kirikiri.service.mapper;

import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.service.dto.roadmap.RoadmapNodesSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodesSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;

public class RoadmapMapper {

    public static RoadmapSaveDto convertToRoadmapSaveDto(final RoadmapSaveRequest request) {
        return new RoadmapSaveDto(request.categoryId(), request.title(), request.introduction(), request.content(),
                RoadmapDifficulty.valueOf(request.difficulty().name()), request.requiredPeriod(),
                request.roadmapNodes().stream()
                        .map(RoadmapMapper::converToRoadmapNodesSaveDto)
                        .toList());
    }

    private static RoadmapNodesSaveDto converToRoadmapNodesSaveDto(final RoadmapNodesSaveRequest request) {
        return new RoadmapNodesSaveDto(request.title(), request.content());
    }
}
