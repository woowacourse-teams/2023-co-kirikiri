package co.kirikiri.service.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequest(
        
        @NotBlank
        String refreshToken
) {

}
