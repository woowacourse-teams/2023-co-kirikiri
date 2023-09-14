package co.kirikiri.service.mapper;

import co.kirikiri.service.dto.auth.OauthRedirectResponse;

public class OauthMapper {

    public static OauthRedirectResponse convertToOauthRedirectDto(final String url, final String state) {
        return new OauthRedirectResponse(url, state);
    }
}
