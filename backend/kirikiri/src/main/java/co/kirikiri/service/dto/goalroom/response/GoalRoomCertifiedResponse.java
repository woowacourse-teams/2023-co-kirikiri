package co.kirikiri.service.dto.goalroom.response;

import java.util.List;

public record GoalRoomCertifiedResponse(
        String name,
        List<GoalRoomNodeResponse> goalRoomNodes,
        int period,
        boolean isJoined
) {

}
