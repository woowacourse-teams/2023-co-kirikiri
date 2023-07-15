package co.kirikiri.service;

import co.kirikiri.domain.auth.RefreshToken;
import co.kirikiri.domain.auth.vo.EncryptedToken;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.persistence.auth.RefreshTokenRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.auth.LoginDto;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public AuthenticationResponse login(final LoginRequest loginRequest) {
        final LoginDto loginDto = AuthMapper.convertToLoginDto(loginRequest);
        final Member member = findMember(loginDto);
        checkPassword(loginDto.password(), member);
        final String rawRefreshToken = tokenProvider.createRefreshToken(member.getIdentifier().getValue(), Map.of());
        saveRefreshToken(member, rawRefreshToken);
        final String accessToken = tokenProvider.createAccessToken(member.getIdentifier().getValue(), Map.of());
        return AuthMapper.convertToAuthenticateResponse(rawRefreshToken, accessToken);
    }

    private Member findMember(final LoginDto loginDto) {
        return memberRepository.findByIdentifier(loginDto.identifier())
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 아이디입니다."));
    }

    private void checkPassword(final Password password, final Member member) {
        if (member.passwordNotMatch(password)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void saveRefreshToken(final Member member, final String rawRefreshToken) {
        final EncryptedToken encryptedToken = new EncryptedToken(rawRefreshToken);
        final LocalDateTime expiredAt = tokenProvider.findTokenExpiredAt(rawRefreshToken);
        final RefreshToken refreshToken = new RefreshToken(encryptedToken, expiredAt, member);
        refreshTokenRepository.save(refreshToken);
    }
}
