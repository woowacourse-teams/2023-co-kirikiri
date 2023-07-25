package co.kirikiri.service.dto.roadmap;

import jakarta.validation.constraints.NotNull;

public record RoadmapReviewSaveRequest(

        String content,

        @NotNull(message = "별점을 입력해 주세요.")
        Double rate
) {

}

