package co.kirikiri.service.mapper;

import co.kirikiri.domain.auth.vo.EncryptedToken;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.service.dto.auth.LoginDto;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.request.ReissueTokenRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthMapper {

    public static LoginDto convertToLoginDto(final LoginRequest request) {
        return new LoginDto(new Identifier(request.identifier()), new Password(request.password()));
    }

    public static AuthenticationResponse convertToAuthenticationResponse(final String refreshToken, final String accessToken) {
        return new AuthenticationResponse(refreshToken, accessToken);
    }

    public static EncryptedToken convertToEncryptedToken(final ReissueTokenRequest reissueTokenRequest) {
        return new EncryptedToken(reissueTokenRequest.refreshToken());
    }
}
