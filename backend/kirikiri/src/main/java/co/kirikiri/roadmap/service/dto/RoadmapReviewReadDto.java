package co.kirikiri.roadmap.service.dto;

import java.time.LocalDateTime;

public record RoadmapReviewReadDto(
        Long id,
        MemberDto member,
        LocalDateTime createdAt,
        String content,
        Double rate
) {

}
