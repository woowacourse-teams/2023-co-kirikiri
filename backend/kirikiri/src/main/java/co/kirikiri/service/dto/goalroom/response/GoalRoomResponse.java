package co.kirikiri.service.dto.goalroom.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalRoomResponse(
        String name,
        String status,
        List<GoalRoomNodeResponse> goalRoomNodes,
        int period,
        Boolean isJoined
) {

    public GoalRoomResponse(
            final String name,
            final List<GoalRoomNodeResponse> goalRoomNodes,
            final int period,
            final Boolean isJoined
    ) {
        this(name, null, goalRoomNodes, period, isJoined);
    }

}