package co.kirikiri.service.dto.goalroom;

import java.util.List;

public record GoalRoomResponse(
        String name,
        List<GoalRoomNodeResponse> goalRoomNodes,
        int period
) {

}
