package co.kirikiri.service.dto.auth.request;

public record AuthenticateResponse(
        String refreshToken,
        String accessToken
) {

}
