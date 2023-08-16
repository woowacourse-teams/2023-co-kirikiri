package co.kirikiri.service.dto.roadmap.request;

public record RoadmapSearchRequest(
        String roadmapTitle,
        String creatorName,
        String tagName
) {

}
