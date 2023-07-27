package co.kirikiri.integration;

import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.request.ReissueTokenRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import co.kirikiri.service.dto.member.request.GenderType;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationIntegrationTest extends IntegrationTest {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";

    @Test
    void 정상적으로_로그인에_성공한다() {
        //given
        회원가입();
        final LoginRequest 로그인_요청 = new LoginRequest(IDENTIFIER, PASSWORD);

        //when
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);

        //then
        assertThat(로그인_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        assertThat(로그인_응답_바디.accessToken()).isNotEmpty();
        assertThat(로그인_응답_바디.refreshToken()).isNotEmpty();
    }

    @Test
    void 회원이_존재하지_않을때_로그인에_실패한다() {
        //given
        final LoginRequest 로그인_요청 = new LoginRequest(IDENTIFIER, PASSWORD);

        //when
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);

        //then
        assertThat(로그인_응답.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        final ErrorResponse 실패_응답_바디 = 로그인_응답.as(ErrorResponse.class);
        assertThat(실패_응답_바디.message()).isEqualTo("존재하지 않는 아이디입니다.");
    }

    @Test
    void 비밀번호가_틀렸을때_로그인에_실패한다() {
        //given
        회원가입();
        final LoginRequest 로그인_요청 = new LoginRequest(IDENTIFIER, "wrongpassword1!");

        //when
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);

        //then
        assertThat(로그인_응답.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        final ErrorResponse 실패_응답_바디 = 로그인_응답.as(ErrorResponse.class);
        assertThat(실패_응답_바디.message()).isEqualTo("비밀번호가 일치하지 않습니다.");
    }

    @Test
    void 아이디와_비밀번호에_빈값이_있을때() throws JsonProcessingException {
        //given
        final LoginRequest 로그인_요청 = new LoginRequest("", "");

        //when
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);

        //then
        assertThat(로그인_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final String responseBody = 로그인_응답.asString();
        final List<ErrorResponse> 로그인_응답_바디 = jsonToClass(responseBody, new TypeReference<>() {
        });
        final ErrorResponse 아이디_빈값_오류_메세지 = new ErrorResponse("아이디는 빈 값일 수 없습니다.");
        final ErrorResponse 비밀번호_빈값_오류_메세지 = new ErrorResponse("비밀번호는 빈 값일 수 없습니다.");

        assertThat(로그인_응답_바디).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(아이디_빈값_오류_메세지, 비밀번호_빈값_오류_메세지));
    }

    @Test
    void 정상적으로_토큰_재발행을_힌다() {
        //given
        회원가입();
        final LoginRequest 로그인_요청 = new LoginRequest(IDENTIFIER, PASSWORD);
        final ExtractableResponse<Response> 로그인_응답 = 로그인(로그인_요청);
        final AuthenticationResponse 로그인_응답_바디 = 로그인_응답.as(AuthenticationResponse.class);
        final ReissueTokenRequest 토큰_재발행_요청 = new ReissueTokenRequest(로그인_응답_바디.refreshToken());

        //when
        final ExtractableResponse<Response> 토큰_재발행_응답 = 토큰_재발행(토큰_재발행_요청);

        //then
        assertThat(토큰_재발행_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        final AuthenticationResponse 토큰_재발행_응답_바디 = 토큰_재발행_응답.as(AuthenticationResponse.class);
        assertThat(토큰_재발행_응답_바디.accessToken()).isNotEmpty();
        assertThat(토큰_재발행_응답_바디.refreshToken()).isNotEmpty();
    }

    @Test
    void 토큰_재발행_시_빈값을_보낼때() throws UnsupportedEncodingException, JsonProcessingException {
        //given
        final ReissueTokenRequest 토큰_재발행_요청 = new ReissueTokenRequest("");

        //when
        final ExtractableResponse<Response> 토큰_재발행_응답 = 토큰_재발행(토큰_재발행_요청);

        //then
        assertThat(토큰_재발행_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final ErrorResponse errorResponse = new ErrorResponse("리프레시 토큰은 빈 값일 수 없습니다.");
        final List<ErrorResponse> 에러_메세지_바디 = jsonToClass(토큰_재발행_응답.asString(), new TypeReference<>() {
        });
        assertThat(에러_메세지_바디).usingRecursiveComparison()
                .isEqualTo(List.of(errorResponse));
    }

    @Test
    void 토큰_재발행_시_유효하지_않은_리프레시_토큰을_보낼때() {
        //given
        final ReissueTokenRequest 토큰_재발행_요청 = new ReissueTokenRequest("anyString");

        //when
        final ExtractableResponse<Response> 토큰_재발행_응답 = 토큰_재발행(토큰_재발행_요청);

        //then
        assertThat(토큰_재발행_응답.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        final ErrorResponse 에러_메세지_바디 = 토큰_재발행_응답.as(ErrorResponse.class);
        assertThat(에러_메세지_바디.message()).isEqualTo("Invalid Token");
    }

    void 회원가입() {
        final MemberJoinRequest 회원가입_요청 = new MemberJoinRequest(IDENTIFIER, PASSWORD, "nickname", "010-1234-5678",
                GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(회원가입_요청)
                .post(API_PREFIX + "/members/join")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 로그인(final LoginRequest 로그인_요청) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(로그인_요청)
                .post(API_PREFIX + "/auth/login")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 토큰_재발행(final ReissueTokenRequest 토큰_재발행_요청) {
        final ExtractableResponse<Response> 토큰_재발행_응답 = given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(토큰_재발행_요청)
                .post(API_PREFIX + "/auth/reissue")
                .then()
                .log().all()
                .extract();
        return 토큰_재발행_응답;
    }
}
