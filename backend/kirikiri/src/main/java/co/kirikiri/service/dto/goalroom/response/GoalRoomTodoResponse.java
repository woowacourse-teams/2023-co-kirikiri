package co.kirikiri.service.dto.goalroom.response;

import java.time.LocalDate;

public record GoalRoomTodoResponse(
        String content,
        LocalDate startDate,
        LocalDate endDate
) {

}
