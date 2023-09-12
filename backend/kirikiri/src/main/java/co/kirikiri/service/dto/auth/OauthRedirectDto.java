package co.kirikiri.service.dto.auth;

public record OauthRedirectDto(
        String url,
        String state
) {
}
