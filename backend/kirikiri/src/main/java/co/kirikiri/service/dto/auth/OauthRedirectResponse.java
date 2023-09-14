package co.kirikiri.service.dto.auth;

public record OauthRedirectResponse(
        String url,
        String state
) {
}
