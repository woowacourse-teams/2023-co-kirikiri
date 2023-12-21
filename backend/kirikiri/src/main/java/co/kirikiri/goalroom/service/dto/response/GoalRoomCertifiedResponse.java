package co.kirikiri.goalroom.service.dto.response;

import java.util.List;

public record GoalRoomCertifiedResponse(
        String name,
        Integer currentMemberCount,
        Integer limitedMemberCount,
        List<GoalRoomRoadmapNodeResponse> goalRoomNodes,
        int period,
        boolean isJoined
) {

}
