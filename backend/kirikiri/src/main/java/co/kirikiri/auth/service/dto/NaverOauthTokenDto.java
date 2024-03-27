package co.kirikiri.auth.service.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NaverOauthTokenDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer expiresIn
) {
}
