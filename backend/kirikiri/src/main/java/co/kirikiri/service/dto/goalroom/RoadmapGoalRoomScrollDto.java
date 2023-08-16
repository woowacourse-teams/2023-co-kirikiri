package co.kirikiri.service.dto.goalroom;

import java.util.List;

public record RoadmapGoalRoomScrollDto(
        List<RoadmapGoalRoomDto> roadmapGoalRoomDtos,
        boolean hasNext
) {

}
