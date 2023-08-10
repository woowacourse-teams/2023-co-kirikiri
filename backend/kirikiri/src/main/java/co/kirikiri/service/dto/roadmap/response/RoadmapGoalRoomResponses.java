package co.kirikiri.service.dto.roadmap.response;

import java.util.List;

public record RoadmapGoalRoomResponses(
        List<RoadmapGoalRoomResponse> responses,
        boolean hasNext
) {

}
