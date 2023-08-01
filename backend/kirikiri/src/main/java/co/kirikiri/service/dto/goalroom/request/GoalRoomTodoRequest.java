package co.kirikiri.service.dto.goalroom.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record GoalRoomTodoRequest(
        @NotBlank(message = "투두의 컨텐츠는 빈 값일 수 없습니다.")
        String content,

        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyyMMdd")
        LocalDate endDate
) {

}
