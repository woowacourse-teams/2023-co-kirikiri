package co.kirikiri.roadmap.service.event;

import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.service.dto.RoadmapSaveDto;

public record RoadmapCreateEvent(
        Roadmap roadmap,
        RoadmapSaveDto roadmapSaveDto
) {

}
