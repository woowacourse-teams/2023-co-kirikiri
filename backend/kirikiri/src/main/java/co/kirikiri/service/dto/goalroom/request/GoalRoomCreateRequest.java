package co.kirikiri.service.dto.goalroom.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record GoalRoomCreateRequest(

        @NotBlank(message = "컨텐츠 아이디는 빈 값일 수 없습니다.")
        Long contentId,

        @NotBlank(message = "골룸 이름을 빈 값일 수 없습니다.")
        String name,

        @NotBlank(message = "골룸 제한 인원은 빈 값일 수 없습니다.")
        Integer limitedMemberCount,

        @NotEmpty(message = "최초 todo는 필수 값입니다.")
        GoalRoomTodoRequest goalRoomTodo,

        @NotEmpty(message = "각 노드에 대한 기간은 필수 값입니다.")
        List<GoalRoomRoadmapNodePeriodRequest> goalRoomRoadmapNodePeriods
) {
}
