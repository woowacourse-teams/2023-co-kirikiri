package co.kirikiri.service.dto.goalroom;

import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
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
