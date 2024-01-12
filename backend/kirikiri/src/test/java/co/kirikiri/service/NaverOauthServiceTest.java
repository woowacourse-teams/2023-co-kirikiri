package co.kirikiri.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.auth.AuthService;
import co.kirikiri.service.auth.NaverOauthService;
import co.kirikiri.service.dto.auth.NaverMemberProfileDto;
import co.kirikiri.service.dto.auth.NaverMemberProfileResponseDto;
import co.kirikiri.service.dto.auth.NaverOauthTokenDto;
import co.kirikiri.service.dto.auth.OauthRedirectResponse;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NaverOauthServiceTest {

    private static final String OAUTH_NAVER_REDIRECT_URL_PROPERTY = "oauth.naver.redirect-url";
    private static final String OAUTH_NAVER_CALLBACK_URL_PROPERTY = "oauth.naver.callback-url";
    private static final String OAUTH_NAVER_CLIENT_ID_PROPERTY = "oauth.naver.client-id";

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private AuthService authService;

    @Mock
    private OauthNetworkService oauthNetworkService;

    @Mock
    private Environment environment;

    @InjectMocks
    private NaverOauthService naverOauthService;

    @Test
    void 정상적으로_Oauth_url을_생성한다() {
        //given
        when(environment.getProperty(OAUTH_NAVER_REDIRECT_URL_PROPERTY))
                .thenReturn("https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&state=%s");
        when(environment.getProperty(OAUTH_NAVER_CLIENT_ID_PROPERTY))
                .thenReturn("clientId");
        when(environment.getProperty(OAUTH_NAVER_CALLBACK_URL_PROPERTY))
                .thenReturn("http://localhost:8080/api/auth/oauth/login/callback");

        //when
        final OauthRedirectResponse result = naverOauthService.makeOauthUrl();

        //then
        assertThat(result.url()).contains("https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=clientId&redirect_uri=http://localhost:8080/api/auth/oauth/login/callback&state=");
    }

    @Test
    void oauth로_회원가입_했던_회원의_경우_정상적으로_naver_oauth_로그인을_한다() {
        //given
        final Map<String, String> queryParams = Map.of("state", "state", "code", "code");
        final NaverOauthTokenDto naverOauthTokenDto = new NaverOauthTokenDto("accessToken", "refreshToken", "tokenType", 1000);
        when(oauthNetworkService.requestToken(NaverOauthTokenDto.class, queryParams))
                .thenReturn(ResponseEntity.ok(naverOauthTokenDto));

        final NaverMemberProfileResponseDto naverMemberProfileResponseDto = new NaverMemberProfileResponseDto("id", "email", "nickname", "M");
        final NaverMemberProfileDto naverMemberProfileDto = new NaverMemberProfileDto("resultCode", "message", naverMemberProfileResponseDto);
        when(oauthNetworkService.requestMemberInfo(NaverMemberProfileDto.class, Map.of(HttpHeaders.AUTHORIZATION, "Bearer accessToken")))
                .thenReturn(ResponseEntity.ok(naverMemberProfileDto));

        final Member member = new Member(1L, null, null, null, null, null, null);
        when(memberRepository.findByOauthId(anyString()))
                .thenReturn(Optional.of(member));

        final AuthenticationResponse authenticationResponse = new AuthenticationResponse("refreshToken", "accessToken");
        when(authService.oauthLogin(member))
                .thenReturn(authenticationResponse);

        //when
        final AuthenticationResponse result = naverOauthService.login(queryParams);

        //then
        assertThat(result).isEqualTo(authenticationResponse);
    }

    @Test
    void oauth로_회원가입을_하지않은_회원의_경무_naver_oauth_회원가입을_한다() {
        //given
        final Map<String, String> queryParams = Map.of("state", "state", "code", "code");
        final NaverOauthTokenDto naverOauthTokenDto = new NaverOauthTokenDto("accessToken", "refreshToken", "tokenType", 1000);
        when(oauthNetworkService.requestToken(NaverOauthTokenDto.class, queryParams))
                .thenReturn(ResponseEntity.ok(naverOauthTokenDto));

        final NaverMemberProfileResponseDto naverMemberProfileResponseDto = new NaverMemberProfileResponseDto("id", "email", "nickname", "M");
        final NaverMemberProfileDto naverMemberProfileDto = new NaverMemberProfileDto("resultCode", "message", naverMemberProfileResponseDto);
        when(oauthNetworkService.requestMemberInfo(NaverMemberProfileDto.class, Map.of(HttpHeaders.AUTHORIZATION, "Bearer accessToken")))
                .thenReturn(ResponseEntity.ok(naverMemberProfileDto));

        when(memberRepository.findByOauthId(anyString()))
                .thenReturn(Optional.empty());

        final AuthenticationResponse authenticationResponse = new AuthenticationResponse("refreshToken", "accessToken");
        when(memberService.oauthJoin(any()))
                .thenReturn(authenticationResponse);

        //when
        final AuthenticationResponse result = naverOauthService.login(queryParams);

        //then
        assertThat(result).isEqualTo(authenticationResponse);
    }
}
