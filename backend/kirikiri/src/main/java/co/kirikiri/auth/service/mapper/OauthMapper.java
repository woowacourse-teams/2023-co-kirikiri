package co.kirikiri.auth.service.mapper;

import co.kirikiri.auth.service.dto.OauthRedirectResponse;

public class OauthMapper {

    public static OauthRedirectResponse convertToOauthRedirectDto(final String url, final String state) {
        return new OauthRedirectResponse(url, state);
    }
}
