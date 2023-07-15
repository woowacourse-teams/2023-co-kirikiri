package co.kirikiri.service.mapper.auth;

import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.service.dto.auth.LoginDto;
import co.kirikiri.service.dto.auth.request.AuthenticateResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;

public class AuthMapper {

    public static LoginDto convertToLoginDto(final LoginRequest request) {
        return new LoginDto(new Identifier(request.identifier()), new Password(request.password()));
    }

    public static AuthenticateResponse convertToAuthenticateResponse(final String refreshToken, final String accessToken) {
        return new AuthenticateResponse(refreshToken, accessToken);
    }
}
