package co.kirikiri.service.dto.auth;

public record AuthenticationTokenDto(
        String accessToken,
        String refreshToken
) {
}
