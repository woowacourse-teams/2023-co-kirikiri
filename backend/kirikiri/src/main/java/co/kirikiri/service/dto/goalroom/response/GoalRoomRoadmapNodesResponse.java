package co.kirikiri.service.dto.goalroom.response;

import java.util.List;

public record GoalRoomRoadmapNodesResponse(
        boolean hasFrontNode,
        boolean hasBackNode,
        List<GoalRoomRoadmapNodeResponse> nodes
) {

}
