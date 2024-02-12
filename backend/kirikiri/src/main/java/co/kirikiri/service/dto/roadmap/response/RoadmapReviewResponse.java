package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.member.service.dto.response.MemberResponse;
import java.time.LocalDateTime;

public record RoadmapReviewResponse(
        Long id,
        MemberResponse member,
        LocalDateTime createdAt,
        String content,
        Double rate
) {

}
