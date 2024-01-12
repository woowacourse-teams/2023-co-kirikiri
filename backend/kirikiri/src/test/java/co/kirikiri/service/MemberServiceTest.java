package co.kirikiri.service;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberImage;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.persistence.auth.RefreshTokenRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.auth.TokenProvider;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.OauthMemberJoinDto;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberInformationForPublicResponse;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import co.kirikiri.service.exception.ConflictException;
import co.kirikiri.service.exception.NotFoundException;
import co.kirikiri.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    private static final String IMAGE_DEFAULT_ORIGINAL_FILE_NAME_PROPERTY = "image.default.originalFileName";
    private static final String IMAGE_DEFAULT_SERVER_FILE_PATH_PROPERTY = "image.default.serverFilePath";
    private static final String IMAGE_DEFAULT_IMAGE_CONTENT_TYPE_PROPERTY = "image.default.imageContentType";

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Environment environment;

    @Mock
    private NumberGenerator numberGenerator;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private MemberService memberService;

    @Test
    void 회원가입을_한다() {
        //given
        final MemberJoinRequest request = new MemberJoinRequest("identifier1", "password1!", "nickname",
                GenderType.MALE, "kirikiri@email.com");

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.empty());
        given(memberRepository.save(any()))
                .willReturn(new Member(1L, null, null, null, null, null, null));
        given(environment.getProperty(IMAGE_DEFAULT_ORIGINAL_FILE_NAME_PROPERTY))
                .willReturn("default-member-image");
        given(environment.getProperty(IMAGE_DEFAULT_SERVER_FILE_PATH_PROPERTY))
                .willReturn("https://blog.kakaocdn.net/dn/GHYFr/btrsSwcSDQV/UQZxkayGyAXrPACyf0MaV1/img.jpg");
        given(environment.getProperty(IMAGE_DEFAULT_IMAGE_CONTENT_TYPE_PROPERTY))
                .willReturn("JPG");
        given(numberGenerator.generate())
                .willReturn(7);

        //when
        //then
        assertThat(memberService.join(request))
                .isEqualTo(1L);
    }

    @Test
    void 회원가입_시_이미_존재하는_아이디가_존재할때_예외를_던진다() {
        //given
        final MemberJoinRequest request = new MemberJoinRequest("identifier1", "password1!", "nickname",
                GenderType.MALE, "kirikiri@email.com");
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";

        final Member member = new Member(identifier, new EncryptedPassword(password), nickname, null,
                new MemberProfile(Gender.MALE, "kirikiri@email.com"));
        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(member));

        //when
        //then
        assertThatThrownBy(() -> memberService.join(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void 로그인한_사용자_자신의_정보를_조회한다() throws MalformedURLException {
        // given
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final Nickname nickname = new Nickname("nickname");
        final MemberImage memberImage = new MemberImage("originalFileName", "serverFilePath", ImageContentType.PNG);
        final Member member = new Member(1L, identifier, null, new EncryptedPassword(password), nickname, memberImage,
                new MemberProfile(Gender.MALE, "kirikiri@email.com"));

        given(memberRepository.findWithMemberProfileAndImageByIdentifier(any()))
                .willReturn(Optional.of(member));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        // when
        final MemberInformationResponse response = memberService.findMemberInformation(identifier.getValue());

        // then
        final MemberInformationResponse expected = new MemberInformationResponse(1L, "nickname",
                "http://example.com/serverFilePath",
                Gender.MALE.name(),
                "identifier1", "kirikiri@email.com");

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void 로그인한_사용자_자신의_정보를_조회할때_존재하지_않는_회원일_경우_예외가_발생한다() {
        // given
        final Identifier identifier = new Identifier("identifier1");

        given(memberRepository.findWithMemberProfileAndImageByIdentifier(any()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> memberService.findMemberInformation(identifier.getValue()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @Test
    void 특정_사용자의_정보를_조회한다() throws MalformedURLException {
        // given
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        final MemberImage memberImage = new MemberImage("originalFileName", "serverFilePath", ImageContentType.PNG);
        final Member member = new Member(identifier, new EncryptedPassword(password), nickname, memberImage,
                new MemberProfile(Gender.MALE, "kirikiri@email.com"));

        given(memberRepository.findWithMemberProfileAndImageById(any()))
                .willReturn(Optional.of(member));
        given(fileService.generateUrl(anyString(), any()))
                .willReturn(new URL("http://example.com/serverFilePath"));

        // when
        final MemberInformationForPublicResponse response = memberService.findMemberInformationForPublic(1L);

        // then
        final MemberInformationForPublicResponse expected = new MemberInformationForPublicResponse("nickname",
                "http://example.com/serverFilePath",
                Gender.MALE.name());

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void 특정_사용자의_정보를_조회할때_로그인한_사용자가_존재하지_않는_회원이면_예외가_발생한다() {
        // given
        // when
        // then
        assertThatThrownBy(() -> memberService.findMemberInformationForPublic(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다.");
    }

    @Test
    void 특정_사용자의_정보를_조회할때_조회하려는_사용자가_존재하지_않는_회원이면_예외가_발생한다() throws MalformedURLException {
        // given
        final Identifier identifier = new Identifier("identifier1");
        final Password password = new Password("password1!");
        final Nickname nickname = new Nickname("nickname");
        final String phoneNumber = "010-1234-5678";
        final MemberImage memberImage = new MemberImage("originalFileName", "serverFilePath", ImageContentType.PNG);
        final Member member = new Member(identifier, new EncryptedPassword(password), nickname, memberImage,
                new MemberProfile(Gender.MALE, "kirikiri@email.com"));

        given(memberRepository.findWithMemberProfileAndImageById(any()))
                .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> memberService.findMemberInformationForPublic(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 회원입니다. memberId = 1");
    }

    @Test
    void oauth_회원가입을_한다() {
        //given
        final OauthMemberJoinDto request = new OauthMemberJoinDto("oauthId", "kirikiri@email.com", "nickname",
                GenderType.UNDEFINED);

        given(memberRepository.save(any()))
                .willReturn(new Member(1L, null, null, null, null, null, null));
        given(environment.getProperty(IMAGE_DEFAULT_ORIGINAL_FILE_NAME_PROPERTY))
                .willReturn("default-member-image");
        given(environment.getProperty(IMAGE_DEFAULT_SERVER_FILE_PATH_PROPERTY))
                .willReturn("https://blog.kakaocdn.net/dn/GHYFr/btrsSwcSDQV/UQZxkayGyAXrPACyf0MaV1/img.jpg");
        given(environment.getProperty(IMAGE_DEFAULT_IMAGE_CONTENT_TYPE_PROPERTY))
                .willReturn("JPG");
        given(numberGenerator.generate())
                .willReturn(7);
        given(tokenProvider.createRefreshToken(any(), any()))
                .willReturn("refreshToken");
        given(tokenProvider.createAccessToken(any(), any()))
                .willReturn("accessToken");

        //when
        final AuthenticationResponse result = memberService.oauthJoin(request);

        //then
        assertThat(result).isEqualTo(new AuthenticationResponse("refreshToken", "accessToken"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"k", "kk", "kkk", "kkkk"})
    void oauth_회원가입_시_이메일의_아이디와_UUID_길이의_합이_40이하일때_회원을_생성한다(final String value) {
        //given
        final OauthMemberJoinDto request = new OauthMemberJoinDto("oauthId", value + "@email.com", "nickname",
                GenderType.UNDEFINED);

        given(memberRepository.save(any()))
                .willReturn(new Member(1L, null, null, null, null, null, null));
        given(environment.getProperty(IMAGE_DEFAULT_ORIGINAL_FILE_NAME_PROPERTY))
                .willReturn("default-member-image");
        given(environment.getProperty(IMAGE_DEFAULT_SERVER_FILE_PATH_PROPERTY))
                .willReturn("https://blog.kakaocdn.net/dn/GHYFr/btrsSwcSDQV/UQZxkayGyAXrPACyf0MaV1/img.jpg");
        given(environment.getProperty(IMAGE_DEFAULT_IMAGE_CONTENT_TYPE_PROPERTY))
                .willReturn("JPG");
        given(numberGenerator.generate())
                .willReturn(7);
        given(tokenProvider.createRefreshToken(any(), any()))
                .willReturn("refreshToken");
        given(tokenProvider.createAccessToken(any(), any()))
                .willReturn("accessToken");

        //when
        final AuthenticationResponse result = memberService.oauthJoin(request);

        //then
        assertThat(result).isEqualTo(new AuthenticationResponse("refreshToken", "accessToken"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"kkkkk", "kkkkkk", "kkkkkkkkkkkkkkkkk"})
    void oauth_회원가입_시_이메일의_아이디와_UUID_길이의_합이_40초과일때_회원을_생성한다(final String value) {
        //given
        final OauthMemberJoinDto request = new OauthMemberJoinDto("oauthId", value + "@email.com", "nickname",
                GenderType.UNDEFINED);

        given(memberRepository.save(any()))
                .willReturn(new Member(1L, null, null, null, null, null, null));
        given(environment.getProperty(IMAGE_DEFAULT_ORIGINAL_FILE_NAME_PROPERTY))
                .willReturn("default-member-image");
        given(environment.getProperty(IMAGE_DEFAULT_SERVER_FILE_PATH_PROPERTY))
                .willReturn("https://blog.kakaocdn.net/dn/GHYFr/btrsSwcSDQV/UQZxkayGyAXrPACyf0MaV1/img.jpg");
        given(environment.getProperty(IMAGE_DEFAULT_IMAGE_CONTENT_TYPE_PROPERTY))
                .willReturn("JPG");
        given(numberGenerator.generate())
                .willReturn(7);
        given(tokenProvider.createRefreshToken(any(), any()))
                .willReturn("refreshToken");
        given(tokenProvider.createAccessToken(any(), any()))
                .willReturn("accessToken");

        //when
        final AuthenticationResponse result = memberService.oauthJoin(request);

        //then
        assertThat(result).isEqualTo(new AuthenticationResponse("refreshToken", "accessToken"));
    }
}
