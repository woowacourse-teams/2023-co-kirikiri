package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.member.response.MemberInformationForPublicResponse;
import co.kirikiri.service.dto.member.response.MemberInformationResponse;
import co.kirikiri.service.exception.NotFoundException;
import co.kirikiri.service.member.MemberService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(MemberController.class)
class MemberReadApiTest extends ControllerTestHelper {

    @MockBean
    private MemberService memberService;

    @Test
    void 로그인한_사용자_자신의_정보를_조회한다() throws Exception {
        // given
        final MemberInformationResponse expected = new MemberInformationResponse(1L, "nickname", "serverFilePath",
                Gender.MALE.name(),
                "identifier1", "kirikiri1@email.com");

        given(memberService.findMemberInformation(any()))
                .willReturn(expected);

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/members/me")
                        .contextPath(API_PREFIX)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "access-token")))
                .andExpect(status().isOk())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("id").description("사용자 oauthId (PK)"),
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("profileImageUrl").description("사용자 이미지 Url"),
                                fieldWithPath("gender").description("사용자 성별"),
                                fieldWithPath("identifier").description("사용자 아이디"),
                                fieldWithPath("email").description("사용자 이메일")
                        )))
                .andReturn();

        // then
        final MemberInformationResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void 로그인한_사용자_자신의_정보를_조회할때_존재하지_않은_회원이면_예외_발생() throws Exception {
        // given
        when(memberService.findMemberInformation(any()))
                .thenThrow(new NotFoundException("존재하지 않는 회원입니다."));

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/members/me")
                        .contextPath(API_PREFIX)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "access-token")))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("존재하지 않는 회원입니다."))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(fieldWithPath("message").description("예외 메시지"))))
                .andReturn();

        // then
        final ErrorResponse errorResponse = jsonToClass(mvcResult, new TypeReference<>() {
        });
        final ErrorResponse expected = new ErrorResponse("존재하지 않는 회원입니다.");

        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 특정_사용자의_정보를_조회한다() throws Exception {
        // given
        final MemberInformationForPublicResponse expected = new MemberInformationForPublicResponse("nickname",
                "serverFilePath",
                Gender.MALE.name());

        given(memberService.findMemberInformationForPublic(any()))
                .willReturn(expected);

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/members/{memberId}", 1L)
                        .contextPath(API_PREFIX)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "access-token")))
                .andExpect(status().isOk())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("profileImageUrl").description("사용자 이미지 Url"),
                                fieldWithPath("gender").description("사용자 성별")
                        )))
                .andReturn();

        // then
        final MemberInformationForPublicResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void 특정_사용자_정보_조회시_조회할_사용자가_없는_회원이면_예외_발생() throws Exception {
        // given
        when(memberService.findMemberInformationForPublic(any()))
                .thenThrow(new NotFoundException("존재하지 않는 회원입니다. memberId = 2"));

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/members/{memberId}", 2L)
                        .contextPath(API_PREFIX)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "access-token")))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("존재하지 않는 회원입니다. memberId = 2"))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")
                        ),
                        responseFields(fieldWithPath("message").description("예외 메시지"))))
                .andReturn();

        // then
        final ErrorResponse errorResponse = jsonToClass(mvcResult, new TypeReference<>() {
        });
        final ErrorResponse expected = new ErrorResponse("존재하지 않는 회원입니다. memberId = 2");

        assertThat(errorResponse)
                .isEqualTo(expected);
    }
}
