package co.kirikiri.todo.service.dto.response;

import java.time.LocalDate;

public record GoalRoomTodoResponse(
        Long id,
        String content,
        LocalDate startDate,
        LocalDate endDate,
        GoalRoomToDoCheckResponse check
) {

}
