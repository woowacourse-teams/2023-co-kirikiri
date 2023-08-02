package co.kirikiri.service.dto.roadmap;

import co.kirikiri.domain.member.Member;

public record RoadmapReviewDto(
        String content,
        Double rate,
        Member member
) {

}
