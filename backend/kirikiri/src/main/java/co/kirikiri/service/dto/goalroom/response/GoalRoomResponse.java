package co.kirikiri.service.dto.goalroom.response;

import java.util.List;

public record GoalRoomResponse(
        String name,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        List<GoalRoomRoadmapNodeResponse> goalRoomNodes,
        int period
) {

}
