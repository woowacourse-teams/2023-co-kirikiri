package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.auth.RefreshTokenRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.auth.AuthService;
import co.kirikiri.service.auth.TokenProvider;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.request.ReissueTokenRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.exception.AuthenticationException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static Member member;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeAll
    static void setUp() {
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final EncryptedPassword encryptedPassword = new EncryptedPassword(password);
        final Nickname nickname = new Nickname("nickname");
        final String email = "kirikiri1@email";
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, email);
        member = new Member(identifier, encryptedPassword, nickname, null, memberProfile);
    }

    @Test
    void 정상적으로_로그인을_한다() {
        //given
        final LoginRequest loginRequest = new LoginRequest("identifier1", "password1!");
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(tokenProvider.createAccessToken(any(), any()))
                .willReturn(accessToken);
        given(tokenProvider.createRefreshToken(any(), any()))
                .willReturn(refreshToken);

        //when
        final AuthenticationResponse authenticationResponse = authService.login(loginRequest);

        //then
        assertThat(authenticationResponse).isEqualTo(
                new AuthenticationResponse(refreshToken, accessToken));
    }

    @Test
    void 존재하지_않는_아이디로_로그인_하는_경우_예외를_던진다() {
        //given
        final LoginRequest loginRequest = new LoginRequest("identifier1", "password1!");
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 일치하지_않는_비밀번호로_로그인_하는_경우_예외를_던진다() {
        //given
        final LoginRequest loginRequest = new LoginRequest("identifier1", "wrongpassword1!");
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));

        //when
        //then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 정상적으로_토큰을_재발행한다() {
        //given
        final String accessToken = "accessToken";
        final String refreshToken = "refreshToken";
        final ReissueTokenRequest reissueTokenRequest = new ReissueTokenRequest("refreshToken");

        given(tokenProvider.isValidToken(any()))
                .willReturn(true);
        given(tokenProvider.createAccessToken(any(), any()))
                .willReturn(accessToken);
        given(tokenProvider.createRefreshToken(any(), any()))
                .willReturn(refreshToken);
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(refreshTokenRepository.findMemberIdentifierByRefreshToken(any()))
                .willReturn(Optional.of(refreshToken));

        //when
        final AuthenticationResponse authenticationResponse = authService.reissueToken(reissueTokenRequest);

        //then
        assertThat(authenticationResponse).isEqualTo(new AuthenticationResponse(refreshToken, accessToken));
    }

    @Test
    void 리프레시_토큰이_유효하지_않을_경우_예외를_던진다() {
        //given
        final String refreshToken = "refreshToken";
        final ReissueTokenRequest reissueTokenRequest = new ReissueTokenRequest(refreshToken);
        given(tokenProvider.isValidToken(any()))
                .willReturn(false);

        //when
        //then
        assertThatThrownBy(() -> authService.reissueToken(reissueTokenRequest))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 리프레시_토큰이_만료_됐을_경우_예외를_던진다() {
        //given
        final String refreshToken = "refreshToken";
        final ReissueTokenRequest reissueTokenRequest = new ReissueTokenRequest(refreshToken);
        given(tokenProvider.isValidToken(any()))
                .willReturn(true);
        given(refreshTokenRepository.findMemberIdentifierByRefreshToken(any()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> authService.reissueToken(reissueTokenRequest))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 리프레시_토큰으로_조회한_회원이_존재하지_않는_경우_예외를_던진다() {
        //given
        final String refreshToken = "refreshToken";
        final ReissueTokenRequest reissueTokenRequest = new ReissueTokenRequest(refreshToken);
        given(tokenProvider.isValidToken(any()))
                .willReturn(true);
        given(refreshTokenRepository.findMemberIdentifierByRefreshToken(any()))
                .willReturn(Optional.of(member.getIdentifier().getValue()));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> authService.reissueToken(reissueTokenRequest))
                .isInstanceOf(AuthenticationException.class);
    }
}
