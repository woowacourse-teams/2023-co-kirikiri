package co.kirikiri.roadmap.service.dto;

public record RoadmapReviewDto(
        String content,
        Double rate,
        Long memberId
) {

}
