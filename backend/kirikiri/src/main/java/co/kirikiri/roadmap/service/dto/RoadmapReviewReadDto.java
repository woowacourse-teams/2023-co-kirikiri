package co.kirikiri.roadmap.service.dto;

import co.kirikiri.service.dto.member.MemberDto;
import java.time.LocalDateTime;

public record RoadmapReviewReadDto(
        Long id,
        MemberDto member,
        LocalDateTime createdAt,
        String content,
        Double rate
) {

}
