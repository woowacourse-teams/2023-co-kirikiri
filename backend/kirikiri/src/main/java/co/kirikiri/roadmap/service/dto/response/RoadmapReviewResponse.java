package co.kirikiri.roadmap.service.dto.response;

import java.time.LocalDateTime;

public record RoadmapReviewResponse(
        Long id,
        MemberResponse member,
        LocalDateTime createdAt,
        String content,
        Double rate
) {

}