package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ForbiddenException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.roadmap.RoadmapReviewSaveRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.snippet.Attributes.Attribute;

@WebMvcTest(RoadmapController.class)
public class RoadmapCreateApiTest extends ControllerTestHelper {

    @MockBean
    private RoadmapService roadmapService;

    @Test
    void 로드맵의_리뷰를_생성한다() throws Exception {
        // given
        doNothing().when(roadmapService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isCreated())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("로드맵 리뷰 내용")
                                        .attributes(new Attribute(RESTRICT, "- 길이 : 0 ~ 1000")),
                                fieldWithPath("rate").description("로드맵 리뷰 별점")
                                        .attributes(new Attribute(RESTRICT, "- 0.0부터 5.0까지, 0.5 단위의 값"))
                                        .optional()),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디"))));
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_null이라면_예외가_발생한다() throws Exception {
        // given
        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest(" ", null);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(RestDocumentationRequestBuilders
                        .post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$[0].message").value("별점을 입력해 주세요."))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("로드맵 리뷰 내용"),
                                fieldWithPath("rate").description("로드맵 리뷰 별점")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final List<ErrorResponse> errorResponses = objectMapper.readValue(response, new TypeReference<>() {
        });
        final List<ErrorResponse> expected = List.of(new ErrorResponse("별점을 입력해 주세요."));
        assertThat(errorResponses)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_리뷰_생성시_별점이_잘못된_값이면_예외가_발생한다() throws Exception {
        // given
        doThrow(new BadRequestException("별점은 0부터 5까지 0.5 단위로 설정할 수 있습니다."))
                .when(roadmapService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.5);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(RestDocumentationRequestBuilders
                        .post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("별점은 0부터 5까지 0.5 단위로 설정할 수 있습니다."))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("로드맵 리뷰 내용"),
                                fieldWithPath("rate").description("로드맵 리뷰 별점")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("별점은 0부터 5까지 0.5 단위로 설정할 수 있습니다.");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_리뷰_생성시_내용이_1000자가_넘으면_예외가_발생한다() throws Exception {
        // given
        doThrow(new BadRequestException("리뷰는 최대 1000글자까지 입력할 수 있습니다."))
                .when(roadmapService)
                .createReview(any(), any(), any());

        final String content = "a".repeat(1001);
        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest(content, 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(RestDocumentationRequestBuilders
                        .post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("리뷰는 최대 1000글자까지 입력할 수 있습니다."))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("로드맵 리뷰 내용"),
                                fieldWithPath("rate").description("로드맵 리뷰 별점")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("리뷰는 최대 1000글자까지 입력할 수 있습니다.");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_리뷰_생성시_존재하지_않은_로드맵이면_예외가_발생한다() throws Exception {
        // given
        doThrow(new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 1L"))
                .when(roadmapService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(RestDocumentationRequestBuilders
                        .post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("존재하지 않는 로드맵입니다. roadmapId = 1L"))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("로드맵 리뷰 내용"),
                                fieldWithPath("rate").description("로드맵 리뷰 별점")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("존재하지 않는 로드맵입니다. roadmapId = 1L");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_리뷰_생성시_완료한_골룸이_없으면_예외가_발생한다() throws Exception {
        // given
        doThrow(new ForbiddenException("로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = 1L memberIdentifier = cokirikiri"))
                .when(roadmapService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(RestDocumentationRequestBuilders
                        .post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message")
                                .value("로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = 1L memberIdentifier = cokirikiri"))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("로드맵 리뷰 내용"),
                                fieldWithPath("rate").description("로드맵 리뷰 별점")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse(
                "로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = 1L memberIdentifier = cokirikiri");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_리뷰_생성시_로드맵_생성자가_리뷰를_달려고_하면_예외가_발생한다() throws Exception {
        // given
        doThrow(new BadRequestException("로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = 1L memberId = 1L"))
                .when(roadmapService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(RestDocumentationRequestBuilders
                        .post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message")
                                .value("로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = 1L memberId = 1L"))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("로드맵 리뷰 내용"),
                                fieldWithPath("rate").description("로드맵 리뷰 별점")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse(
                "로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = 1L memberId = 1L");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_리뷰_생성시_이미_리뷰를_단적이_있으면_예외가_발생한다() throws Exception {
        // given
        doThrow(new BadRequestException("이미 작성한 리뷰가 존재합니다. roadmapId = 1L memberId = 1L"))
                .when(roadmapService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(RestDocumentationRequestBuilders
                        .post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message")
                                .value("이미 작성한 리뷰가 존재합니다. roadmapId = 1L memberId = 1L"))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("content").description("로드맵 리뷰 내용"),
                                fieldWithPath("rate").description("로드맵 리뷰 별점")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse(
                "이미 작성한 리뷰가 존재합니다. roadmapId = 1L memberId = 1L");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }
}
