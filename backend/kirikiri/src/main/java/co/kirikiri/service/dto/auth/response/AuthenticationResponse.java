package co.kirikiri.service.dto.auth.response;

public record AuthenticationResponse(
        String refreshToken,
        String accessToken
) {

}
