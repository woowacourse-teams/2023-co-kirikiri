package co.kirikiri.service.dto.goalroom;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalRoomResponse(
        String name,
        List<GoalRoomNodeResponse> goalRoomNodes,
        int period,
        Boolean isJoined
) {

}
