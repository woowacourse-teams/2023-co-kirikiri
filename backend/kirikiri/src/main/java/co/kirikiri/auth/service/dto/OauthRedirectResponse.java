package co.kirikiri.auth.service.dto;

public record OauthRedirectResponse(
        String url,
        String state
) {
}
