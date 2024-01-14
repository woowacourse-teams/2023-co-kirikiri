package co.kirikiri.goalroom.service.dto;

import java.time.LocalDate;

public record GoalRoomRoadmapNodeDto(
        Long roadmapNodeId,
        int checkCount,
        LocalDate startDate,
        LocalDate endDate
) {

}
