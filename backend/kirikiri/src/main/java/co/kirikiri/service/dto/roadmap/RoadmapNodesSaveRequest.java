package co.kirikiri.service.dto.roadmap;

import jakarta.validation.constraints.NotNull;

public record RoadmapNodesSaveRequest(

        @NotNull(message = "로드맵 노드의 제목을 입력해주세요.")
        String title,

        @NotNull(message = "로드맵 노드 설명을 입력해주세요.")
        String content
) {

}
