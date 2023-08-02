package co.kirikiri.service.dto.roadmap.request;

public record RoadmapSearchRequest(
        String roadmapTitle,
        Long creatorId,
        String tagName
) {

}
