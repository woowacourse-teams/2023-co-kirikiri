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
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.MemberService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.member.response.MemberMyInfoResponse;
import co.kirikiri.service.dto.member.response.MemberPublicInfoResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(MemberController.class)
public class MemberReadApiTest extends ControllerTestHelper {

    @MockBean
    private MemberService memberService;

    @Test
    void 로그인한_사용자_자신의_정보를_조회한다() throws Exception {
        // given
        final MemberMyInfoResponse expected = new MemberMyInfoResponse("nickname", "serverFilePath", Gender.MALE.name(),
                "identifier1", "010-1234-5678", LocalDate.now());

        given(memberService.findMyInfo(any()))
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
                                fieldWithPath("nickname").description("사용자 닉네임"),
                                fieldWithPath("profileImageUrl").description("사용자 이미지 Url"),
                                fieldWithPath("gender").description("사용자 성별"),
                                fieldWithPath("identifier").description("사용자 아이디"),
                                fieldWithPath("phoneNumber").description("사용자 전화번호"),
                                fieldWithPath("birthday").description("사용자 생년월일")
                        )))
                .andReturn();

        // then
        final MemberMyInfoResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void 로그인한_사용자_자신의_정보를_조회할때_존재하지_않은_회원이면_예외가_발생한다() throws Exception {
        // given
        when(memberService.findMyInfo(any()))
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
        final MemberPublicInfoResponse expected = new MemberPublicInfoResponse("nickname", "serverFilePath",
                Gender.MALE.name());

        given(memberService.findMemberPublicInfo(any(), any()))
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
        final MemberPublicInfoResponse response = jsonToClass(mvcResult, new TypeReference<>() {
        });

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void 특정_사용자의_정보를_조회할때_로그인한_사용자가_존재하지_않은_회원이면_예외가_발생한다() throws Exception {
        // given
        when(memberService.findMemberPublicInfo(any(), any()))
                .thenThrow(new NotFoundException("존재하지 않는 회원입니다."));

        // when
        final MvcResult mvcResult = mockMvc.perform(get(API_PREFIX + "/members/{memberId}", 1L)
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
    void 특정_사용자의_정보를_조회할때_조회할_사용자가_존재하지_않은_회원이면_예외가_발생한다() throws Exception {
        // given
        when(memberService.findMemberPublicInfo(any(), any()))
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
