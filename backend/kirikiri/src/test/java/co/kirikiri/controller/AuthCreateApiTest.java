package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.controller.helper.FieldDescriptionHelper.FieldDescription;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.service.AuthService;
import co.kirikiri.service.NaverOauthService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.auth.OauthRedirectResponse;
import co.kirikiri.service.dto.auth.request.LoginRequest;
import co.kirikiri.service.dto.auth.request.ReissueTokenRequest;
import co.kirikiri.service.dto.auth.response.AuthenticationResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import java.util.List;

@WebMvcTest(AuthController.class)
class AuthCreateApiTest extends ControllerTestHelper {

    private static final String IDENTIFIER = "identifier1";
    private static final String PASSWORD = "password1!";

    @MockBean
    private AuthService authService;

    @MockBean
    private NaverOauthService naverOauthService;

    @Test
    void 정상적으로_로그인에_성공한다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest(IDENTIFIER, PASSWORD);
        final AuthenticationResponse expectedResponse = new AuthenticationResponse("refreshToken", "accessToken");
        final String jsonRequest = objectMapper.writeValueAsString(request);
        given(authService.login(request))
                .willReturn(expectedResponse);

        final List<FieldDescription> requestFieldDescription = makeLoginSuccessRequestFieldDescription();
        final List<FieldDescription> responseFieldDescription = makeLoginSuccessResponseFieldDescription();

        //when
        final MvcResult mvcResult = 로그인(jsonRequest, status().isOk())
                .andDo(documentationResultHandler.document(
                        requestFields(makeFieldDescriptor(requestFieldDescription)),
                        responseFields(makeFieldDescriptor(responseFieldDescription)))
                )
                .andReturn();

        //then
        final AuthenticationResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 로그인_시_아이디에_빈값이_들어올_때_예외를_던진다() throws Exception {
        //given
        final LoginRequest request = new LoginRequest("", PASSWORD);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 로그인(jsonRequest, status().isBadRequest())
                .andReturn();

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
        final LoginRequest request = new LoginRequest(IDENTIFIER, "");
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 로그인(jsonRequest, status().isBadRequest())
                .andReturn();

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
        final MvcResult mvcResult = 로그인(jsonRequest, status().isBadRequest())
                .andReturn();

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
        final LoginRequest request = new LoginRequest(IDENTIFIER, PASSWORD);
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new AuthenticationException("존재하지 않는 아이디입니다."))
                .when(authService)
                .login(request);

        //when
        final MvcResult mvcResult = 로그인(jsonRequest, status().isUnauthorized())
                .andReturn();

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
        final LoginRequest request = new LoginRequest(IDENTIFIER, PASSWORD);
        final String jsonRequest = objectMapper.writeValueAsString(request);
        doThrow(new AuthenticationException("비밀번호가 일치하지 않습니다."))
                .when(authService)
                .login(request);

        //when
        final MvcResult mvcResult = 로그인(jsonRequest, status().isUnauthorized())
                .andReturn();

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
        final AuthenticationResponse expectedResponse = new AuthenticationResponse("reIssuedRefreshToken",
                "reIssuedAccessToken");
        final String jsonRequest = objectMapper.writeValueAsString(request);
        given(authService.reissueToken(request))
                .willReturn(expectedResponse);

        //when
        final MvcResult mvcResult = 토큰_재발행(jsonRequest, status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                requestFields(
                                        fieldWithPath("refreshToken").description("리프레시 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("refreshToken").description("리프레시 토큰"),
                                        fieldWithPath("accessToken").description("액세스 토큰")
                                )
                        )
                )
                .andReturn();

        //then
        final AuthenticationResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void 토큰_재발행_시_리프레시_토큰이_빈값일_때_예외를_던진다() throws Exception {
        //given
        final ReissueTokenRequest request = new ReissueTokenRequest("");
        final String jsonRequest = objectMapper.writeValueAsString(request);

        //when
        final MvcResult mvcResult = 토큰_재발행(jsonRequest, status().isBadRequest())
                .andReturn();

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
        final MvcResult mvcResult = 토큰_재발행(jsonRequest, status().isUnauthorized())
                .andReturn();

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
        final MvcResult mvcResult = 토큰_재발행(jsonRequest, status().isUnauthorized())
                .andReturn();

        //then
        final ErrorResponse errorResponse = new ErrorResponse("Expired Token");
        final ErrorResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });
        assertThat(response).usingRecursiveComparison()
                .isEqualTo(errorResponse);
    }

    @Test
    void 네이버_로그인_페이지를_정상적으로_반환한다() throws Exception {
        // given
        final OauthRedirectResponse expectedResponse = new OauthRedirectResponse("Naver_Login_Redirect_Page_URL", "state");
        given(naverOauthService.makeOauthUrl()).willReturn(expectedResponse);

        네이버_로그인_페이지(status().isOk());
    }

    @Test
    void 네이버에서_콜백요청을_받아_사용자_정보를_반환한다() throws Exception {
        // given
        final AuthenticationResponse expectedResponse = new AuthenticationResponse("refresh_token", "access_token");
        given(naverOauthService.login(any()))
                .willReturn(expectedResponse);

        네이버_사용자_정보_요청(status().isOk());
    }

    private ResultActions 로그인(final String jsonRequest, final ResultMatcher result) throws Exception {
        return mockMvc.perform(post(API_PREFIX + "/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
                .andDo(print());
    }

    private ResultActions 토큰_재발행(final String jsonRequest, final ResultMatcher result) throws Exception {
        return mockMvc.perform(post(API_PREFIX + "/auth/reissue")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
                .andDo(print());
    }

    private ResultActions 네이버_로그인_페이지(final ResultMatcher result) throws Exception {
        return mockMvc.perform(get(API_PREFIX + "/auth/oauth/naver")
                        .param("code", "code")
                        .param("state", "state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
                .andDo(print());
    }

    private ResultActions 네이버_사용자_정보_요청(final ResultMatcher result) throws Exception {
        return mockMvc.perform(get(API_PREFIX + "/auth/login/oauth")
                        .param("code", "code")
                        .param("state", "state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(API_PREFIX))
                .andExpect(result)
                .andDo(print());
    }

    private List<FieldDescription> makeLoginSuccessRequestFieldDescription() {
        return List.of(
                new FieldDescription("identifier", "사용자 아이디",
                        "- 길이 : 4 ~ 20  +" + "\n" +
                                "- 영어 소문자, 숫자 가능"),
                new FieldDescription("password", "사용자 비밀번호",
                        "- 길이 : 8 ~ 15  +" + "\n" +
                                "- 영어 소문자, 숫자, 특수문자  +" + "\n" +
                                "- 특수문자[!,@,#,$,%,^,&,*,(,),~] 사용 가능")
        );
    }

    private List<FieldDescription> makeLoginSuccessResponseFieldDescription() {
        return List.of(
                new FieldDescription("refreshToken", "리프레시 토큰"),
                new FieldDescription("accessToken", "액세스 토큰")
        );
    }
}
