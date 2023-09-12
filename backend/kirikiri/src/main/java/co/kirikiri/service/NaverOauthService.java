package co.kirikiri.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.auth.NaverMemberProfileDto;
import co.kirikiri.service.dto.auth.NaverMemberProfileResponseDto;
import co.kirikiri.service.dto.auth.NaverOauthTokenDto;
import co.kirikiri.service.dto.auth.OauthRedirectDto;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.OauthMemberJoinDto;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.mapper.OauthMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class NaverOauthService {

    private static final String BEARER = "Bearer ";

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AuthService authService;
    private final OauthNetworkService oauthNetworkService;
    private final String redirectUrl;
    private final String callBackUrl;
    private final String clientId;

    public NaverOauthService(final MemberRepository memberRepository,
                             final MemberService memberService,
                             final AuthService authService,
                             final OauthNetworkService oauthNetworkService,
                             @Value("${oauth.naver.redirect-url}") final String redirectUrl,
                             @Value("${oauth.naver.callback-url}") final String callBackUrl,
                             @Value("${oauth.naver.client-id}") final String clientId) {
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.authService = authService;
        this.oauthNetworkService = oauthNetworkService;
        this.redirectUrl = redirectUrl;
        this.callBackUrl = callBackUrl;
        this.clientId = clientId;
    }

    public OauthRedirectDto makeOauthUrl() {
        final String state = generateState();
        final String url = String.format(redirectUrl, clientId, callBackUrl, state);
        return OauthMapper.convertToOauthRedirectDto(url, state);
    }

    private String generateState() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
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
        return oauthNetworkService.requestMemberInfo(NaverMemberProfileDto.class, headers).getBody();
    }
}
