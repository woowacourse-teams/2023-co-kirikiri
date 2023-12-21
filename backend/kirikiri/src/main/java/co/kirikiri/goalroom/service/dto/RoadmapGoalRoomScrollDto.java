package co.kirikiri.goalroom.service.dto;

import java.util.List;

public record RoadmapGoalRoomScrollDto(
        List<RoadmapGoalRoomDto> roadmapGoalRoomDtos,
        boolean hasNext
) {

}
