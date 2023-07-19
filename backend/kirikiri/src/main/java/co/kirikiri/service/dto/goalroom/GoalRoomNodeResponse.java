package co.kirikiri.service.dto.goalroom;

import java.time.LocalDate;

public record GoalRoomNodeResponse(
        String title,
        LocalDate startDate,
        LocalDate endDate,
        int checkCount
) {

}
