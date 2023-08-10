package co.kirikiri.service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public record CustomReviewScrollRequest(
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
        LocalDateTime lastCreatedAt,
        Double lastReviewRate,
        @NotNull(message = "사이즈를 입력해 주세요.")
        Integer size
) {

}
