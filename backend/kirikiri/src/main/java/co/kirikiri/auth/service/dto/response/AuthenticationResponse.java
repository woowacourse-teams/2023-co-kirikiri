package co.kirikiri.auth.service.dto.response;

public record AuthenticationResponse(
        String refreshToken,
        String accessToken
) {

}
