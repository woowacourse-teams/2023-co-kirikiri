package co.kirikiri.service.dto.goalroom;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record CheckFeedDto(
        @NotBlank(message = "인증 피드용 사진은 빈 값일 수 없습니다.")
        MultipartFile image,
        String description
) {

}
