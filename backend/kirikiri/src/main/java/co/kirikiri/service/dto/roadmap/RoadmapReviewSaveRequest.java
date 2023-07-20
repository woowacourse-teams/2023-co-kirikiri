package co.kirikiri.service.dto.roadmap;

import jakarta.validation.constraints.NotBlank;

public record RoadmapReviewSaveRequest(

        @NotBlank(message = "리뷰를 입력해 주세요.")
        String content,
        
        Double rate
) {

}

