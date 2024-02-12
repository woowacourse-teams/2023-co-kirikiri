package co.kirikiri.service.dto.roadmap;

import co.kirikiri.member.service.dto.MemberDto;
import java.time.LocalDateTime;

public record RoadmapReviewReadDto(
        Long id,
        MemberDto member,
        LocalDateTime createdAt,
        String content,
        Double rate
) {

}
