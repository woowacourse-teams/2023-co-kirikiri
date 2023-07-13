package co.kirikiri.integration;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.service.dto.member.GenderType;
import co.kirikiri.service.dto.member.request.JoinMemberRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class MemberIntegrationTest extends IntegrationTest {

    @Test
    void 정상적으로_회원가입을_성공한다() {
        //given
        final JoinMemberRequest request = new JoinMemberRequest("ab12", "password12!@#$%", "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }


    @ParameterizedTest
    @ValueSource(strings = {"abc", "abcdefghijklmnopqrstu"})
    void 아이디_길이가_틀린_경우_회원가입에_실패한다(final String identifier) {
        //given
        final JoinMemberRequest request = new JoinMemberRequest(identifier, "password12!", "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcd", "abcdefghijklmnopqrsT", "가나다라"})
    void 아이디에_허용되지_않은_문자가_들어온_경우_회원가입에_실패한다(final String identifier) {
        //given
        final JoinMemberRequest request = new JoinMemberRequest(identifier, "password12!", "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 아이디가_중복된_경우_회원가입에_실패한다() {
        //given
        final JoinMemberRequest request = new JoinMemberRequest("ab12", "password12!", "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));
        회원가입을_한다(request);

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcde1!", "abcdefghijklmn12"})
    void 비밀번호_길이가_틀린_경우_회원가입에_실패한다(final String password) {
        //given
        final JoinMemberRequest request = new JoinMemberRequest("ab12", password, "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdef1/", "abcdefghij1₩", "abcdefgH1!"})
    void 비밀번호에_허용되지_않은_문자가_들어온_경우_회원가입에_실패한다(final String password) {
        //given
        final JoinMemberRequest request = new JoinMemberRequest("ab12", password, "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefgh", "abcdefghijkl"})
    void 비밀번호에_영소문자만_들어온_경우_회원가입에_실패한다(final String password) {
        //given
        final JoinMemberRequest request = new JoinMemberRequest("ab12", password, "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678", "12345678910"})
    void 비밀번호에_숫자만_들어온_경우_회원가입에_실패한다(final String password) {
        //given
        final JoinMemberRequest request = new JoinMemberRequest("ab12", password, "nickname", "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "abcdefghi"})
    void 닉네임_길이가_틀린_경우_회원가입에_실패한다(final String nickname) {
        //given
        final JoinMemberRequest request = new JoinMemberRequest("ab12", "password12!@#$%", nickname, "010-1234-5678",
            GenderType.MALE, LocalDate.of(2023, Month.JULY, 12));

        //when
        final ExtractableResponse<Response> response = 회원가입을_한다(request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 회원가입을_한다(final JoinMemberRequest request) {
        return given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .body(request)
            .post("/api/member/join")
            .then()
            .log().all()
            .extract();
    }
}
