package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.ImageContentType;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberImage;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberMyInfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class MemberIntegrationTest extends IntegrationTest {

    private final MemberRepository memberRepository;

    public MemberIntegrationTest(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Test
    void 정상적으로_회원가입을_성공한다() {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!@#$%", "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "abcdefghijklmnopqrstu"})
    void 아이디_길이가_틀린_경우_회원가입에_실패한다(final String 회원_아이디) {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(회원_아이디, "password12!", "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        final ErrorResponse 에러_메세지 = 회원가입_응답.as(ErrorResponse.class);
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(에러_메세지.message()).isEqualTo("제약 조건에 맞지 않는 아이디입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcd", "abcdefghijklmnopqrsT", "가나다라"})
    void 아이디에_허용되지_않은_문자가_들어온_경우_회원가입에_실패한다(final String 회원_아이디) {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(회원_아이디, "password12!", "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        final ErrorResponse 에러_메세지 = 회원가입_응답.as(ErrorResponse.class);
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(에러_메세지.message()).isEqualTo("제약 조건에 맞지 않는 아이디입니다.");
    }

    @Test
    void 아이디가_중복된_경우_회원가입에_실패한다() {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!", "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        회원가입(회원가입_요청);

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        final ErrorResponse 에러_메세지 = 회원가입_응답.as(ErrorResponse.class);
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(에러_메세지.message()).isEqualTo("이미 존재하는 아이디입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcde1!", "abcdefghijklmn12"})
    void 비밀번호_길이가_틀린_경우_회원가입에_실패한다(final String password) {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", password, "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        final ErrorResponse 에러_메세지 = 회원가입_응답.as(ErrorResponse.class);
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(에러_메세지.message()).isEqualTo("제약 조건에 맞지 않는 비밀번호입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdef1/", "abcdefghij1₩", "abcdefgH1!"})
    void 비밀번호에_허용되지_않은_문자가_들어온_경우_회원가입에_실패한다(final String password) {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", password, "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        final ErrorResponse 에러_메세지 = 회원가입_응답.as(ErrorResponse.class);
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(에러_메세지.message()).isEqualTo("제약 조건에 맞지 않는 비밀번호입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefgh", "abcdefghijkl"})
    void 비밀번호에_영소문자만_들어온_경우_회원가입에_실패한다(final String password) {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", password, "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        final ErrorResponse 에러_메세지 = 회원가입_응답.as(ErrorResponse.class);
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(에러_메세지.message()).isEqualTo("제약 조건에 맞지 않는 비밀번호입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "12345678910"})
    void 비밀번호에_숫자만_들어온_경우_회원가입에_실패한다(final String password) {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", password, "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        final ErrorResponse 에러_메세지 = 회원가입_응답.as(ErrorResponse.class);
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(에러_메세지.message()).isEqualTo("제약 조건에 맞지 않는 비밀번호입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abcdefghi"})
    void 닉네임_길이가_틀린_경우_회원가입에_실패한다(final String nickname) {
        //given
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest("ab12", "password12!@#$%", nickname, "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> 회원가입_응답 = 회원가입(회원가입_요청);

        //then
        final ErrorResponse 에러_메세지 = 회원가입_응답.as(ErrorResponse.class);
        assertThat(회원가입_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(에러_메세지.message()).isEqualTo("제약 조건에 맞지 않는 닉네임입니다.");
    }

    @Test
    void 로그인한_사용자_자신의_정보를_성공적으로_조회한다() throws JsonProcessingException {
        // given
        // TODO: 회원가입 시 이미지 저장하는 로직 추가된 후 회원가입 API 사용하도록 수정
        final String 아이디 = "identifier1";
        final String 비밀번호 = "password1!";

        final MemberImage memberImage = new MemberImage("originalFileName", "serverFilePath", ImageContentType.PNG);
        final Member member = new Member(new Identifier(아이디), new EncryptedPassword(new Password(비밀번호)),
                new Nickname("nickname"), memberImage,
                new MemberProfile(Gender.MALE, LocalDate.now(), "010-1234-5678"));

        memberRepository.save(member);

        final String 액세스_토큰 = 로그인을_하고_액세스_토큰을_받는다(아이디, 비밀번호);

        // when
        final ExtractableResponse<Response> 사용자_자신의_정보_조회_응답 = 사용자_자신의_정보_조회_요청(액세스_토큰);

        // then
        final MemberMyInfoResponse 사용자_자신의_정보_조회_응답_바디 = jsonToClass(사용자_자신의_정보_조회_응답.asString(),
                new TypeReference<>() {
                });
        final MemberMyInfoResponse 예상하는_응답값 = new MemberMyInfoResponse("nickname", "serverFilePath",
                GenderType.MALE.name(), "identifier1", "010-1234-5678", LocalDate.now());

        assertThat(사용자_자신의_정보_조회_응답_바디).isEqualTo(예상하는_응답값);
    }

    private ExtractableResponse<Response> 회원가입(final MemberJoinRequest 회원가입_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post(API_PREFIX + "/members/join")
                .then()
                .log().all()
                .extract();
    }

    private String 로그인을_하고_액세스_토큰을_받는다(final String 아이디, final String 비밀번호) {
        final LoginRequest 로그인_요청 = new LoginRequest(아이디, 비밀번호);

        final AuthenticationResponse 토큰_응답 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청)
                .post(API_PREFIX + "/auth/login")
                .then()
                .log().all()
                .extract()
                .as(new TypeRef<>() {
                });

        return String.format(BEARER_TOKEN_FORMAT, 토큰_응답.accessToken());
    }

    private ExtractableResponse<Response> 사용자_자신의_정보_조회_요청(final String 액세스_토큰) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(HttpHeaders.AUTHORIZATION, 액세스_토큰)
                .get(API_PREFIX + "/members/me")
                .then()
                .log().all()
                .extract();
    }
}
