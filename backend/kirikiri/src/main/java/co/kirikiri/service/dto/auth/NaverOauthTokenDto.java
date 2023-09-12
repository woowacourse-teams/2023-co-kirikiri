package co.kirikiri.service.dto.auth;

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
