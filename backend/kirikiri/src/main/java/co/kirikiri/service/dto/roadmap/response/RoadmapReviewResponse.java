package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.service.dto.member.response.MemberResponse;
import java.time.LocalDateTime;

public record RoadmapReviewResponse(
        Long id,
        MemberResponse member,
        //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime createdAt,
        String content,
        Double rate
) {

}