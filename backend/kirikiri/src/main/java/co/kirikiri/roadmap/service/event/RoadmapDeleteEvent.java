package co.kirikiri.roadmap.service.event;

public record RoadmapDeleteEvent(
        Long roadmapId,
        String target
) {
}
