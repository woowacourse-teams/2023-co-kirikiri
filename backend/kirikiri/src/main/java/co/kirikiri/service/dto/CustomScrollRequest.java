package co.kirikiri.service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CustomScrollRequest (
        LocalDateTime lastCreatedAt,
        Long lastGoalRoomCount,
        Long lastParticipatedCount,
        Double lastReviewRate,
        @NotNull(message = "사이즈를 입력해 주세요.")
        Integer size
) {

}
