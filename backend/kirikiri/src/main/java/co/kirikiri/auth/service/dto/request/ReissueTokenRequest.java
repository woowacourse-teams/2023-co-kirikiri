package co.kirikiri.auth.service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequest(
        @NotBlank(message = "리프레시 토큰은 빈 값일 수 없습니다.")
        String refreshToken
) {

}
