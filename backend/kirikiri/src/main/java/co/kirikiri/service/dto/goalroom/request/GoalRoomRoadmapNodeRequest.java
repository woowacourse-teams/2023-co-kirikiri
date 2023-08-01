package co.kirikiri.service.dto.goalroom.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GoalRoomRoadmapNodeRequest(
        @NotNull(message = "로드맵 노드 아이디는 빈 값일 수 없습니다.")
        Long roadmapNodeId,

        @NotNull(message = "인증 횟수는 빈 값일 수 없습니다.")
        Integer checkCount,

        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate endDate
) {

}
