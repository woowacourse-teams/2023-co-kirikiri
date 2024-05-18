package co.kirikiri.roadmap.service.event;

import co.kirikiri.roadmap.service.dto.RoadmapSaveDto;

public record RoadmapCreateEvent(
        Long roadmapId,
        RoadmapSaveDto roadmapSaveDto
) {

}
