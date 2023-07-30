package co.kirikiri.service.dto.goalroom.response;

import java.time.LocalDate;

public record GoalRoomTodoResponse(
        Long id,
        String content,
        LocalDate startDate,
        LocalDate endDate
) {

}
