package co.kirikiri.goalroom.service.dto;

import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import java.util.List;

public record GoalRoomCreateDto(
        Long roadmapContentId,
        GoalRoomName goalRoomName,
        LimitedMemberCount limitedMemberCount,
        List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos
) {

    public int goalRoomRoadmapNodeDtosSize() {
        return goalRoomRoadmapNodeDtos.size();
    }
}
