package co.kirikiri.roadmap.service.dto.request;

public record RoadmapSearchRequest(
        String roadmapTitle,
        String creatorName,
        String tagName
) {

}
