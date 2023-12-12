package co.kirikiri.integration.fixture;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.API_PREFIX;
import static co.kirikiri.integration.fixture.CommonFixture.AUTHORIZATION;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static io.restassured.RestAssured.given;

import co.kirikiri.auth.service.dto.request.LoginRequest;
import co.kirikiri.member.service.dto.request.GenderType;
import co.kirikiri.member.service.dto.request.MemberJoinRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class MemberAPIFixture {

    public static final String DEFAULT_IDENTIFIER = "identifier1";
    public static final String DEFAULT_PASSWORD = "password1!";
    public static final String DEFAULT_NICKNAME = "nickname";
    public static final GenderType DEFAULT_GENDER_TYPE = GenderType.MALE;
    public static final String DEFAULT_EMAIL = "kirikiri1@email.com";

    public static ExtractableResponse<Response> 요청을_받는_회원가입(final MemberJoinRequest 회원가입_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post(API_PREFIX + "/members/join")
                .then()
                .log().all()
                .extract();
    }

    public static Long 회원가입(final MemberJoinRequest 회원가입_요청) {
        final Response 회원가입_응답 = 요청을_받는_회원가입(회원가입_요청).response();
        final String Location_헤더 = 회원가입_응답.header("Location");
        return Long.parseLong(Location_헤더.substring(13));
    }

    public static Long 기본_회원가입() {
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD, DEFAULT_NICKNAME,
                DEFAULT_GENDER_TYPE, DEFAULT_EMAIL);
        return 회원가입(회원가입_요청);
    }

    public static ExtractableResponse<Response> 요청을_받는_사용자_자신의_정보_조회_요청(final String 액세스_토큰) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, 액세스_토큰)
                .get(API_PREFIX + "/members/me")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 요청을_받는_특정_사용자의_정보_조회(final String 액세스_토큰, final Long 조회할_회원_아이디) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .header(AUTHORIZATION, 액세스_토큰)
                .get(API_PREFIX + "/members/{memberId}", 조회할_회원_아이디)
                .then()
                .log().all()
                .extract();
    }

    public static String 사용자를_추가하고_토큰을_조회한다(final String 아이디, final String 닉네임) {
        final MemberJoinRequest 팔로워_회원_가입_요청 = new MemberJoinRequest(아이디, "paswword2@",
                닉네임, GenderType.FEMALE, DEFAULT_EMAIL);
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        return String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());
    }
}
