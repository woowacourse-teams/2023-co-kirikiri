package co.kirikiri.goalroom.service.dto.response;

import java.time.LocalDate;

public record DashBoardToDoResponse(
        Long id,
        String content,
        LocalDate startDate,
        LocalDate endDate,
        DashBoardToDoCheckResponse check
) {

}
