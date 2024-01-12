package co.kirikiri.service.dto.goalroom.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GoalRoomCreateRequest(

        @NotNull(message = "로드맵 컨텐츠 아이디는 빈 값일 수 없습니다.")
        Long roadmapContentId,

        @NotBlank(message = "골룸 이름을 빈 값일 수 없습니다.")
        String name,

        @NotNull(message = "골룸 제한 인원은 빈 값일 수 없습니다.")
        Integer limitedMemberCount,

        @Valid
        List<GoalRoomRoadmapNodeRequest> goalRoomRoadmapNodeRequests
) {

}
