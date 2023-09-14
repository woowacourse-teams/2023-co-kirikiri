package co.kirikiri.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.auth.NaverMemberProfileDto;
import co.kirikiri.service.dto.auth.NaverMemberProfileResponseDto;
import co.kirikiri.service.dto.auth.NaverOauthTokenDto;
import co.kirikiri.service.dto.auth.OauthRedirectResponse;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.OauthMemberJoinDto;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.mapper.OauthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NaverOauthService {

    private static final String OAUTH_NAVER_REDIRECT_URL_PROPERTY = "oauth.naver.redirect-url";
    private static final String OAUTH_NAVER_CALLBACK_URL_PROPERTY = "oauth.naver.callback-url";
    private static final String OAUTH_NAVER_CLIENT_ID_PROPERTY = "oauth.naver.client-id";
    private static final String BEARER = "Bearer ";

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AuthService authService;
    private final OauthNetworkService oauthNetworkService;
    private final Environment environment;

    public OauthRedirectResponse makeOauthUrl() {
        final String state = generateState();
        final String redirectUrl = getProperty(OAUTH_NAVER_REDIRECT_URL_PROPERTY);
        final String clientId = getProperty(OAUTH_NAVER_CLIENT_ID_PROPERTY);
        final String callBackUrl = getProperty(OAUTH_NAVER_CALLBACK_URL_PROPERTY);
        final String url = String.format(redirectUrl, clientId, callBackUrl, state);
        return OauthMapper.convertToOauthRedirectDto(url, state);
    }

    private String generateState() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private String getProperty(final String property) {
        return environment.getProperty(property);
    }

    @Transactional
    public AuthenticationResponse login(final Map<String, String> queryParams) {
        final NaverOauthTokenDto naverOauthTokenDto = oauthNetworkService.requestToken(NaverOauthTokenDto.class, queryParams).getBody();
        final NaverMemberProfileDto naverMemberProfileDto = getNaverMemberProfileDto(naverOauthTokenDto.accessToken());
        final NaverMemberProfileResponseDto naverMemberProfileResponseDto = naverMemberProfileDto.response();
        final Optional<Member> optionalMember = memberRepository.findByOauthId(naverMemberProfileResponseDto.id());
        if (optionalMember.isPresent()) {
            final Member member = optionalMember.get();
            return authService.oauthLogin(member);
        }
        return memberService.oauthJoin(new OauthMemberJoinDto(naverMemberProfileResponseDto.id(), naverMemberProfileResponseDto.email(),
                naverMemberProfileResponseDto.nickname(), GenderType.findByOauthType(naverMemberProfileResponseDto.gender())));
    }

    private NaverMemberProfileDto getNaverMemberProfileDto(final String accessToken) {
        final Map<String, String> headers = Map.of(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        return oauthNetworkService.requestMemberInfo(NaverMemberProfileDto.class, headers)
                .getBody();
    }
}
