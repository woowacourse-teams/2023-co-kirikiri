package co.kirikiri.roadmap.service.dto.response;

import java.util.List;

public record RoadmapGoalRoomResponses(
        List<RoadmapGoalRoomResponse> responses,
        boolean hasNext
) {

}
