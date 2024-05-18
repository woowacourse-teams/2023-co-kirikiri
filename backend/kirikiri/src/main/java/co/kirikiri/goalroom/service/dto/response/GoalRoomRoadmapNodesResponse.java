package co.kirikiri.goalroom.service.dto.response;

import java.util.List;

public record GoalRoomRoadmapNodesResponse(
        boolean hasFrontNode,
        boolean hasBackNode,
        List<GoalRoomRoadmapNodeResponse> nodes
) {

}
