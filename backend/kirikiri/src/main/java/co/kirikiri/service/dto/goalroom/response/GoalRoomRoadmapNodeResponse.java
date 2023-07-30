package co.kirikiri.service.dto.goalroom.response;

import java.time.LocalDate;

public record GoalRoomRoadmapNodeResponse(
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Integer checkCount
) {

}
