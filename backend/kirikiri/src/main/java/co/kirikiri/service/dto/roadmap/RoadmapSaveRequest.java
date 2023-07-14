package co.kirikiri.service.dto.roadmap;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record RoadmapSaveRequest(

        @NotNull(message = "카테고리를 입력해주세요.")
        Long categoryId,

        @NotNull(message = "로드맵의 제목을 입력해주세요.")
        String title,

        @NotNull(message = "로드맵의 소개글을 입력해주세요.")
        String introduction,

        String content,

        @NotNull(message = "난이도를 입력해주세요.")
        RoadmapDifficultyType difficulty,

        @NotNull(message = "추천 소요 기간을 입력해주세요.")
        @PositiveOrZero(message = "추천 소요 기간은 0이상 이여야 합니다.")
        Integer requiredPeriod,

        @NotEmpty(message = "로드맵의 첫 번째 단계를 입력해주세요.")
        List<@Valid RoadmapNodesSaveRequest> roadmapNodes
) {

}
