package co.kirikiri.service.mapper;

import co.kirikiri.service.dto.auth.OauthRedirectDto;

public class OauthMapper {

    public static OauthRedirectDto convertToOauthRedirectDto(final String url, final String state) {
        return new OauthRedirectDto(url, state);
    }
}
