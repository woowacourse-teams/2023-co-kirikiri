package co.kirikiri.service.dto.roadmap.response;

import co.kirikiri.goalroom.service.dto.response.RoadmapGoalRoomResponse;
import java.util.List;

public record RoadmapGoalRoomResponses(
        List<RoadmapGoalRoomResponse> responses,
        boolean hasNext
) {

}
