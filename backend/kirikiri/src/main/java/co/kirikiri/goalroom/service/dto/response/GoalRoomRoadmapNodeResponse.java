package co.kirikiri.goalroom.service.dto.response;

import java.time.LocalDate;

public record GoalRoomRoadmapNodeResponse(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Integer checkCount
) {

}
