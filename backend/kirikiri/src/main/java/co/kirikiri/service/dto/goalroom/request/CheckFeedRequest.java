package co.kirikiri.service.dto.goalroom.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CheckFeedRequest(
        @NotNull(message = "인증 이미지는 반드시 존재해야 합니다.")
        MultipartFile image,
        String description
) {

}
