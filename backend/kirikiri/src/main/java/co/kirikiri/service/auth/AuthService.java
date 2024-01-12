package co.kirikiri.service.auth;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.auth.RefreshTokenRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.aop.ExceptionConvert;
import co.kirikiri.service.dto.auth.LoginDto;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.request.ReissueTokenRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.exception.AuthenticationException;
import co.kirikiri.service.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ExceptionConvert
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
        saveRefreshToken(refreshToken, member);
        final String accessToken = tokenProvider.createAccessToken(member.getIdentifier().getValue(), Map.of());
        return AuthMapper.convertToAuthenticationResponse(refreshToken, accessToken);
    }

    private void saveRefreshToken(final String refreshToken, final Member member) {
        refreshTokenRepository.save(refreshToken, member.getIdentifier().getValue());
    }

    public AuthenticationResponse oauthLogin(final Member member) {
        return makeAuthenticationResponse(member);
    }

    public boolean isCertified(final String token) {
        return tokenProvider.isValidToken(token);
    }

    @Transactional
    public AuthenticationResponse reissueToken(final ReissueTokenRequest reissueTokenRequest) {
        checkTokenValid(reissueTokenRequest.refreshToken());
        final String memberIdentifier = findMemberIdentifierByRefreshToken(reissueTokenRequest.refreshToken());
        final Member member = findMemberByRefreshToken(memberIdentifier);
        return makeAuthenticationResponse(member);
    }

    private void checkTokenValid(final String token) {
        if (!isCertified(token)) {
            throw new AuthenticationException("토큰이 유효하지 않습니다.");
        }
    }

    private String findMemberIdentifierByRefreshToken(final String clientRefreshToken) {
        return refreshTokenRepository.findMemberIdentifierByRefreshToken(clientRefreshToken)
                .orElseThrow(() -> new AuthenticationException("토큰이 만료 되었습니다."));
    }

    private Member findMemberByRefreshToken(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 회원입니다."));
    }

    public String findIdentifierByToken(final String token) {
        return tokenProvider.findSubject(token);
    }
}
