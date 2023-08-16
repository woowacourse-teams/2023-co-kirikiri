package co.kirikiri.service.event;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;

public record RoadmapCreateEvent(
        Roadmap roadmap,
        RoadmapSaveDto roadmapSaveDto
) {
}
