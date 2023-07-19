package co.kirikiri.service.dto.goalroom.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record GoalRoomTodoRequest(

        @NotBlank
        String content,

        @JsonFormat(pattern = "yyMMdd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyMMdd")
        LocalDate endDate
) {
}
