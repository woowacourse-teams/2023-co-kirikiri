package co.kirikiri.auth.service;

import co.kirikiri.auth.service.dto.NaverMemberProfileDto;
import co.kirikiri.auth.service.dto.NaverMemberProfileResponseDto;
import co.kirikiri.auth.service.dto.NaverOauthTokenDto;
import co.kirikiri.auth.service.dto.response.AuthenticationResponse;
import co.kirikiri.auth.service.dto.response.OauthRedirectResponse;
import co.kirikiri.auth.service.mapper.OauthMapper;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.persistence.MemberRepository;
import co.kirikiri.member.service.MemberService;
import co.kirikiri.member.service.dto.OauthMemberJoinDto;
import co.kirikiri.member.service.dto.request.GenderType;
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
        final NaverOauthTokenDto naverOauthTokenDto = oauthNetworkService.requestToken(NaverOauthTokenDto.class, queryParams)
                .getBody();
        final NaverMemberProfileDto naverMemberProfileDto = getNaverMemberProfileDto(naverOauthTokenDto.accessToken());
        final NaverMemberProfileResponseDto naverMemberProfileResponseDto = naverMemberProfileDto.response();
        final Optional<Member> optionalMember = memberRepository.findByOauthId(naverMemberProfileResponseDto.id());
        final Member savedMember = optionalMember.orElseGet(() -> saveMember(naverMemberProfileResponseDto));
        return authService.oauthLogin(savedMember);
    }

    private Member saveMember(final NaverMemberProfileResponseDto naverMemberProfileResponseDto) {
        return memberService.oauthJoin(
                new OauthMemberJoinDto(naverMemberProfileResponseDto.id(),
                        naverMemberProfileResponseDto.email(),
                        naverMemberProfileResponseDto.nickname(),
                        GenderType.findByOauthType(naverMemberProfileResponseDto.gender())));
    }

    private NaverMemberProfileDto getNaverMemberProfileDto(final String accessToken) {
        final Map<String, String> headers = Map.of(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        return oauthNetworkService.requestMemberInfo(NaverMemberProfileDto.class, headers)
                .getBody();
    }
}
