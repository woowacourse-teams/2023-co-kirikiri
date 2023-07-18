package co.kirikiri.service.dto.roadmap;

import jakarta.validation.constraints.NotBlank;

public record RoadmapNodeSaveRequest(

        @NotBlank(message = "로드맵 노드의 제목을 입력해주세요.")
        String title,

        @NotBlank(message = "로드맵 노드의 설명을 입력해주세요.")
        String content
) {

}
