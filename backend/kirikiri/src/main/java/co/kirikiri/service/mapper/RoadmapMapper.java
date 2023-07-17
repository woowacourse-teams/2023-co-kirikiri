package co.kirikiri.service.mapper;

import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;

public class RoadmapMapper {

    public static RoadmapSaveDto convertToRoadmapSaveDto(final RoadmapSaveRequest request) {
        return new RoadmapSaveDto(request.categoryId(), request.title(), request.introduction(), request.content(),
                request.difficulty(), request.requiredPeriod(),
                request.roadmapNodes().stream()
                        .map(RoadmapMapper::convertToRoadmapNodesSaveDto)
                        .toList());
    }

    private static RoadmapNodeSaveDto convertToRoadmapNodesSaveDto(final RoadmapNodeSaveRequest request) {
        return new RoadmapNodeSaveDto(request.title(), request.content());
    }
}
