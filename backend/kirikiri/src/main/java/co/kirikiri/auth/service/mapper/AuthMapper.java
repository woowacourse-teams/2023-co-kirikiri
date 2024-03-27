package co.kirikiri.auth.service.mapper;

import co.kirikiri.auth.service.dto.LoginDto;
import co.kirikiri.auth.service.dto.request.LoginRequest;
import co.kirikiri.auth.service.dto.response.AuthenticationResponse;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.domain.vo.Password;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthMapper {

    public static LoginDto convertToLoginDto(final LoginRequest request) {
        return new LoginDto(new Identifier(request.identifier()), new Password(request.password()));
    }

    public static AuthenticationResponse convertToAuthenticationResponse(final String refreshToken,
                                                                         final String accessToken) {
        return new AuthenticationResponse(refreshToken, accessToken);
    }
}
