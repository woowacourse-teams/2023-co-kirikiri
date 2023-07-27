package co.kirikiri.service.dto.goalroom;

import java.time.LocalDate;

public record GoalRoomRoadmapNodeDto(
        Long roadmapNodeId,
        int checkCount,
        LocalDate startDate,
        LocalDate endDate
) {
}
