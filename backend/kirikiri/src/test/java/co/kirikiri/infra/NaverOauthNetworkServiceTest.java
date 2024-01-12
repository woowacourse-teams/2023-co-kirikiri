package co.kirikiri.infra;

import co.kirikiri.service.dto.auth.NaverOauthTokenDto;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NaverOauthNetworkServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Environment environment;

    @InjectMocks
    private NaverOauthNetworkService naverOauthNetworkService;

    @Test
    void 정상적으로_네이버_OAuth_토큰을_요청한다() {
        // given
        final ResponseEntity<NaverOauthTokenDto> 예상하는_응답 = ResponseEntity.of(
                Optional.of(new NaverOauthTokenDto("access_token", "refresh_token", "token_type", 3600)));

        when(environment.getProperty("oauth.naver.token-url"))
                .thenReturn("https://nid.naver.com/oauth2.0/token?");
        when(environment.getProperty("oauth.naver.client-id"))
                .thenReturn("client_id");
        when(environment.getProperty("oauth.naver.client-secret"))
                .thenReturn("client_secret");
        when(restTemplate.getForEntity(anyString(), eq(NaverOauthTokenDto.class)))
                .thenReturn(예상하는_응답);

        // when
        final ResponseEntity<NaverOauthTokenDto> 응답 = naverOauthNetworkService.requestToken(
                NaverOauthTokenDto.class, Map.of(
                        "code", "code",
                        "state", "state",
                        "grant_type", "authorization_code"
                ));

        // then
        assertThat(응답.getBody())
                .isEqualTo(예상하는_응답.getBody());
    }

    @Test
    void 정상적으로_네이버_사용자_프로필_정보를_요청한다() {
        // given
        final ResponseEntity<AuthenticationResponse> 예상하는_응답 = ResponseEntity.of(
                Optional.of(new AuthenticationResponse("refresh_token", "access_token")));

        when(environment.getProperty("oauth.naver.member-info-url"))
                .thenReturn("https://openapi.naver.com/v1/nid/me");
        when(restTemplate.exchange(anyString(), any(), any(), eq(AuthenticationResponse.class)))
                .thenReturn(예상하는_응답);

        // when
        final ResponseEntity<AuthenticationResponse> 응답 = naverOauthNetworkService.requestMemberInfo(
                AuthenticationResponse.class, Map.of(HttpHeaders.AUTHORIZATION, "token"));

        // then
        assertThat(응답.getBody())
                .isEqualTo(예상하는_응답.getBody());
    }
}
