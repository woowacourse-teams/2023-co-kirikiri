package co.kirikiri.service.dto.roadmap.request;

import jakarta.validation.constraints.NotBlank;

public record RoadmapCategorySaveRequest(
        
        @NotBlank(message = "카테고리 이름은 빈 값일 수 없습니다.")
        String name
) {

}
