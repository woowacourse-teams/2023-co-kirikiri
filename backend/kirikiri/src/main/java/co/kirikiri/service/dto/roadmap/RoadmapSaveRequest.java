package co.kirikiri.service.dto.roadmap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record RoadmapSaveRequest(

        @NotBlank
        Long categoryId,

        @NotBlank(message = "로드맵의 제목을 입력해주세요.")
        String title,

        @NotBlank(message = "로드맵의 소개글을 입력해주세요.")
        String introduction,

        String content,

        @NotBlank(message = "난이도를 입력해주세요.")
        RoadmapDifficultyType difficulty,

        @PositiveOrZero(message = "추천 소요 기간은 0이상 이여야 합니다.")
        int requiredPeriod,

        @NotBlank
        List<RoadmapNodesSaveRequest> roadmapNodes
) {

}
