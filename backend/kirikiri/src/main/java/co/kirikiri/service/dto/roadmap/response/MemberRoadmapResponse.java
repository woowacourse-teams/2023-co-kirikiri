package co.kirikiri.service.dto.roadmap.response;

public record MemberRoadmapResponse(
        Long roadmapId,
        String roadmapTitle,
        String difficulty,
        RoadmapCategoryResponse category
) {

}
