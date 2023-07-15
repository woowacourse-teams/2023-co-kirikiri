package co.kirikiri.controller;

import co.kirikiri.controller.helper.RestDocsHelper;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.service.AuthService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.request.ReissueTokenRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends RestDocsHelper {

    @MockBean
    private AuthService authService;

    @Test
    void 정상적으로_로그인에_성공한다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("identifier1", "password1!");
        final AuthenticationResponse expectedResponse = new AuthenticationResponse("refreshToken", "accessToken");
        final String jsonRequest = objectMapper.writeValueAsString(request);
        given(authService.login(request))
                .willReturn(expectedResponse);

        //when
        final MvcResult mvcResult = login(jsonRequest, status().isOk());

        //then
        final AuthenticationResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    void 로그인_시_아이디에_빈값이_들어올_때_예외를_던진다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("", "password1!");
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = login(jsonRequest, status().isBadRequest());

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("아이디는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 로그인_시_비밀번호_빈값이_들어올_때_예외를_던진다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("identifier1!", "");
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = login(jsonRequest, status().isBadRequest());

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("비밀번호는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedResponse));
    }

    @Test
    void 로그인_시_아이디와_비밀번호_모두_빈값이_들어올_때_예외를_던진다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("", "");
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = login(jsonRequest, status().isBadRequest());

        //then
        final ErrorResponse passwordResponse = new ErrorResponse("비밀번호는 빈 값일 수 없습니다.");
        final ErrorResponse identifierResponse = new ErrorResponse("아이디는 빈 값일 수 없습니다.");
        final List<ErrorResponse> responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(identifierResponse, passwordResponse));
    }

    @Test
    void 로그인_시_아이디에_해당하는_회원이_없을_때_예외를_던진다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("identifier1", "password1!");
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new AuthenticationException("존재하지 않는 아이디입니다."))
                .when(authService)
                .login(request);

        //when
        final MvcResult mvcResult = login(jsonRequest, status().isUnauthorized());

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("존재하지 않는 아이디입니다.");
        final ErrorResponse responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    void 로그인_시_비밀번호가_맞지_않을_때_예외를_던진다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("identifier1", "password1!");
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new AuthenticationException("비밀번호가 일치하지 않습니다."))
                .when(authService)
                .login(request);

        //when
        final MvcResult mvcResult = login(jsonRequest, status().isUnauthorized());

        //then
        final ErrorResponse expectedResponse = new ErrorResponse("비밀번호가 일치하지 않습니다.");
        final ErrorResponse responses = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(responses).usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    void 토큰을_정상적으로_재발행한다() throws Exception {
        //given
        final ReissueTokenRequest request = new ReissueTokenRequest("refreshToken");
        final AuthenticationResponse expectedResponse = new AuthenticationResponse("reIssuedRefreshToken", "reIssuedAccessToken");
        final String jsonRequest = objectMapper.writeValueAsString(request);
        given(authService.reissueToken(request))
                .willReturn(expectedResponse);

        //when
        final MvcResult mvcResult = reissue(jsonRequest, status().isOk());

        //then
        final AuthenticationResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    void 토큰_재발행_시_리프레시_토큰이_빈값일_때_예외를_던진다() throws Exception {
        //given
        final ReissueTokenRequest request = new ReissueTokenRequest("");
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = reissue(jsonRequest, status().isBadRequest());

        //then
        final ErrorResponse errorResponse = new ErrorResponse("리프레시 토큰은 빈 값일 수 없습니다.");
        final List<ErrorResponse> response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(List.of(errorResponse));
    }

    @Test
    void 토큰_재발행_시_토큰이_유효하지_않을_때_예외를_던진다() throws Exception {
        //given
        final ReissueTokenRequest request = new ReissueTokenRequest("refreshToken");
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new AuthenticationException("Invalid Token"))
                .when(authService)
                .reissueToken(request);

        //when
        final MvcResult mvcResult = reissue(jsonRequest, status().isUnauthorized());

        //then
        final ErrorResponse errorResponse = new ErrorResponse("Invalid Token");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(errorResponse);
    }

    @Test
    void 토큰_재발행_시_토큰이_만료_됐을_때_예외를_던진다() throws Exception {
        //given
        final ReissueTokenRequest request = new ReissueTokenRequest("refreshToken");
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new AuthenticationException("Expired Token"))
                .when(authService)
                .reissueToken(request);

        //when
        final MvcResult mvcResult = reissue(jsonRequest, status().isUnauthorized());

        //then
        final ErrorResponse errorResponse = new ErrorResponse("Expired Token");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(errorResponse);
    }

    private MvcResult login(final String jsonRequest, final ResultMatcher result) throws Exception {
        return mockMvc.perform(post(API_PREFIX + "/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
                .andDo(print())
                .andReturn();
    }

    private MvcResult reissue(final String jsonRequest, final ResultMatcher result) throws Exception {
        return mockMvc.perform(post(API_PREFIX + "/auth/reissue")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
                .andDo(print())
                .andReturn();
    }
}
