package co.kirikiri.service.dto.roadmap.response;

public record RoadmapSummaryResponse(
        Long roadmapId,
        String roadmapTitle,
        String difficulty,
        RoadmapCategoryResponse category
) {

}
