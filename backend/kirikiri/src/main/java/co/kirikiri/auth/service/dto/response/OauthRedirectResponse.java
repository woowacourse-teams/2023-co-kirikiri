package co.kirikiri.auth.service.dto.response;

public record OauthRedirectResponse(
        String url,
        String state
) {
}
