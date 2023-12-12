package co.kirikiri.member.service.dto.response;

import java.time.LocalDate;

public record MemberGoalRoomTodoResponse(
        Long id,
        String content,
        LocalDate startDate,
        LocalDate endDate,
        MemberGoalRoomToDoCheckResponse check
) {
}
