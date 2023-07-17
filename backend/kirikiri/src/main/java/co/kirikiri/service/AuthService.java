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
import co.kirikiri.service.dto.auth.request.ReissueTokenRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public AuthenticationResponse login(final LoginRequest loginRequest) {
        final LoginDto loginDto = AuthMapper.convertToLoginDto(loginRequest);
        final Member member = findMember(loginDto);
        checkPassword(loginDto.password(), member);
        return makeAuthenticationResponse(member);
    }

    private Member findMember(final LoginDto loginDto) {
        return memberRepository.findByIdentifier(loginDto.identifier())
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 아이디입니다."));
    }

    private void checkPassword(final Password password, final Member member) {
        if (member.isPasswordMismatch(password)) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }
    }

    private AuthenticationResponse makeAuthenticationResponse(final Member member) {
        final String refreshToken = tokenProvider.createRefreshToken(member.getIdentifier().getValue(), Map.of());
        saveRefreshToken(member, refreshToken);
        final String accessToken = tokenProvider.createAccessToken(member.getIdentifier().getValue(), Map.of());
        return AuthMapper.convertToAuthenticationResponse(refreshToken, accessToken);
    }

    private void saveRefreshToken(final Member member, final String rawRefreshToken) {
        final EncryptedToken encryptedToken = new EncryptedToken(rawRefreshToken);
        final LocalDateTime expiredAt = tokenProvider.findTokenExpiredAt(rawRefreshToken);
        final RefreshToken refreshToken = new RefreshToken(encryptedToken, expiredAt, member);
        refreshTokenRepository.save(refreshToken);
    }

    public boolean isCertified(final String token) {
        return tokenProvider.isValidToken(token);
    }

    @Transactional
    public AuthenticationResponse reissueToken(final ReissueTokenRequest reissueTokenRequest) {
        checkTokenValid(reissueTokenRequest.refreshToken());
        final EncryptedToken clientRefreshToken = AuthMapper.convertToEncryptedToken(reissueTokenRequest);
        final RefreshToken refreshToken = findSavedRefreshToken(clientRefreshToken);
        checkTokenExpired(refreshToken);
        refreshTokenRepository.delete(refreshToken);
        final Member member = refreshToken.getMember();
        return makeAuthenticationResponse(member);
    }

    private void checkTokenValid(final String token) {
        if (!isCertified(token)) {
            throw new AuthenticationException("토큰이 유효하지 않습니다.");
        }
    }

    private RefreshToken findSavedRefreshToken(final EncryptedToken clientRefreshToken) {
        return refreshTokenRepository.findByTokenAndIsRevokedFalse(clientRefreshToken)
                .orElseThrow(() -> new AuthenticationException("토큰이 유효하지 않습니다."));
    }

    private void checkTokenExpired(final RefreshToken refreshToken) {
        if (refreshToken.isExpired()) {
            throw new AuthenticationException("토큰이 만료 되었습니다.");
        }
    }

    public String findIdentifierByToken(final String token) {
        return tokenProvider.findSubject(token);
    }
}
