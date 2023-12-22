package co.kirikiri.roadmap.service.dto;

import co.kirikiri.domain.member.Member;

public record RoadmapReviewDto(
        String content,
        Double rate,
        Member member
) {

}
