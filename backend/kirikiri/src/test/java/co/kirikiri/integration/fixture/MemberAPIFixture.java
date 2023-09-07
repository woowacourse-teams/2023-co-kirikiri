package co.kirikiri.integration.fixture;

import static co.kirikiri.integration.fixture.AuthenticationAPIFixture.로그인;
import static co.kirikiri.integration.fixture.CommonFixture.API_PREFIX;
import static co.kirikiri.integration.fixture.CommonFixture.AUTHORIZATION;
import static co.kirikiri.integration.fixture.CommonFixture.BEARER_TOKEN_FORMAT;
import static io.restassured.RestAssured.given;

import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.Month;
import org.springframework.http.MediaType;

public class MemberAPIFixture {

    public static final String DEFAULT_IDENTIFIER = "identifier1";
    public static final String DEFAULT_PASSWORD = "password1!";
    public static final String DEFAULT_NICKNAME = "nickname";
    public static final String DEFAULT_PHONE_NUMBER = "010-1234-5678";
    public static final LocalDate DEFAULT_BIRTHDAY = LocalDate.of(2023, Month.JULY, 12);
    public static final GenderType DEFAULT_GENDER_TYPE = GenderType.MALE;
    public static final String DEFAULT_ADMIN_IDENTIFIER = "admin1";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin1234!";
    public static final String DEFAULT_ADMIN_NICKNAME = "admin";
    public static final String DEFAULT_ADMIN_PHONE_NUMBER = "010-9876-5432";

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
                DEFAULT_PHONE_NUMBER, DEFAULT_GENDER_TYPE, DEFAULT_BIRTHDAY);
        return 회원가입(회원가입_요청);
    }

    public static Long 어드민_회원가입() {
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(DEFAULT_ADMIN_IDENTIFIER, DEFAULT_ADMIN_PASSWORD,
                DEFAULT_ADMIN_NICKNAME, DEFAULT_ADMIN_PHONE_NUMBER, DEFAULT_GENDER_TYPE, DEFAULT_BIRTHDAY);
        final Response 회원가입_응답 = 요청을_받는_어드민_회원가입(회원가입_요청).response();
        final String Location_헤더 = 회원가입_응답.header("Location");
        return Long.parseLong(Location_헤더.substring(13));
    }

    public static ExtractableResponse<Response> 요청을_받는_어드민_회원가입(final MemberJoinRequest 회원가입_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post(API_PREFIX + "/members/join/admin")
                .then()
                .log().all()
                .extract();
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
                닉네임, "010-1234-1234", GenderType.FEMALE, LocalDate.of(1999, 9, 9));
        final LoginRequest 팔로워_로그인_요청 = new LoginRequest(팔로워_회원_가입_요청.identifier(), 팔로워_회원_가입_요청.password());
        회원가입(팔로워_회원_가입_요청);
        return String.format(BEARER_TOKEN_FORMAT, 로그인(팔로워_로그인_요청).accessToken());
    }
}
