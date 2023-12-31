package co.kirikiri.service.dto.goalroom.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GoalRoomRoadmapNodeRequest(
        @NotNull(message = "로드맵 노드 아이디는 빈 값일 수 없습니다.")
        Long roadmapNodeId,

        @NotNull(message = "인증 횟수는 빈 값일 수 없습니다.")
        Integer checkCount,

        @NotNull(message = "로드맵 노드 시작 날짜는 빈 값일 수 없습니다.")
        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate startDate,

        @NotNull(message = "로드맵 노드 종료 날짜는 빈 값일 수 없습니다.")
        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate endDate
) {

}
