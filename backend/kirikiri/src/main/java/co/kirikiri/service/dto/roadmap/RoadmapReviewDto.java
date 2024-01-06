package co.kirikiri.service.dto.roadmap;

import co.kirikiri.member.domain.Member;

public record RoadmapReviewDto(
        String content,
        Double rate,
        Member member
) {

}
