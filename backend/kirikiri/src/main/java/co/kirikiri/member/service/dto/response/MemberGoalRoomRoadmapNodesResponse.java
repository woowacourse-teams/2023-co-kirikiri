package co.kirikiri.member.service.dto.response;

import java.util.List;

public record MemberGoalRoomRoadmapNodesResponse(
        boolean hasFrontNode,
        boolean hasBackNode,
        List<MemberGoalRoomRoadmapNodeResponse> nodes
) {
}
