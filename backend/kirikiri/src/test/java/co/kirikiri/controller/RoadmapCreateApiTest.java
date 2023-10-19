package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ConflictException;
import co.kirikiri.exception.ForbiddenException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.roadmap.RoadmapCreateService;
import co.kirikiri.service.roadmap.RoadmapReadService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapCategorySaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.request.RequestPartDescriptor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Attributes.Attribute;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(RoadmapController.class)
class RoadmapCreateApiTest extends ControllerTestHelper {

    @MockBean
    private RoadmapCreateService roadmapCreateService;

    @MockBean
    private RoadmapReadService roadmapReadService;

    @Test
    void 정상적으로_로드맵을_생성한다() throws Exception {
        // given
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willReturn(1L);

        // expect
        로드맵_생성_요청(request, status().isCreated());
    }

    @Test
    void 로드맵_생성시_존재하지_않은_회원이면_예외가_발생한다() throws Exception {
        // given
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new AuthenticationException("존재하지 않는 회원입니다."));

        // expect
        로드맵_생성_요청(request, status().isUnauthorized());
    }

    @Test
    void 로드맵_생성시_유효하지_않은_카테고리_아이디를_입력하면_예외가_발생한다() throws Exception {
        // given
        final Long categoryId = 10L;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(categoryId, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new NotFoundException("존재하지 않는 카테고리입니다. categoryId = 10"));

        // expect
        로드맵_생성_요청(request, status().isNotFound());
    }

    @Test
    void 로드맵_생성시_카테고리_아이디를_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final Long categoryId = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(categoryId, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_제목의_길이가_40보다_크면_예외가_발생한다() throws Exception {
        // given
        final String title = "a".repeat(41);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, title, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 제목의 길이는 최소 1글자, 최대 40글자입니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_제목을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String title = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, title, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_소개글의_길이가_150보다_크면_예외가_발생한다() throws Exception {
        // given
        final String introduction = "a".repeat(151);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", introduction, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 소개글의 길이는 최소 1글자, 최대 150글자입니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_소개글을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String introduction = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", introduction, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_본문의_길이가_2000보다_크면_예외가_발생한다() throws Exception {
        // given
        final String content = "a".repeat(2001);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", content,
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 본문의 길이는 최대 2000글자입니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_난이도를_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final RoadmapDifficultyType difficulty = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                difficulty, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1001})
    void 로드맵_생성시_로드맵_추천_소요기간이_0보다_작거나_1000보다_크면_예외가_발생한다(final int requiredPeriod) throws Exception {
        // given
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, requiredPeriod,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 추천 소요 기간은 최소 0일, 최대 1000일입니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_추천_소요기간을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final Integer requiredPeriod = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, requiredPeriod,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_제목의_길이가_40보다_크면_예외가_발생한다() throws Exception {
        // given
        final String nodeTitle = "a".repeat(41);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest(nodeTitle, "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 노드의 제목의 길이는 최소 1글자, 최대 40글자입니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_설명의_길이가_2000보다_크면_예외가_발생한다() throws Exception {
        // given
        final String nodeContent = "a".repeat(2001);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", nodeContent, null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 노드의 설명의 길이는 최소 1글자, 최대 2000글자입니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_제목을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String nodeTitle = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest(nodeTitle, "로드맵 1주차에는 알고리즘을 배울거에요.", null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_설명을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String nodeContent = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", nodeContent, null)),
                List.of(new RoadmapTagSaveRequest("태그1")));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void 로드맵_생성시_태그_이름이_1미만_10초과면_예외가_발생한다(final int nameLength) throws Exception {
        // given
        final String tagName = "a".repeat(nameLength);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 내용", null)),
                List.of(new RoadmapTagSaveRequest(tagName)));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("태그 이름은 최소 1자부터 최대 10자까지 가능합니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_태그_개수가_5개_초과면_예외가_발생한다() throws Exception {
        // given
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1"), new RoadmapTagSaveRequest("태그2"),
                        new RoadmapTagSaveRequest("태그3"), new RoadmapTagSaveRequest("태그4"),
                        new RoadmapTagSaveRequest("태그5"), new RoadmapTagSaveRequest("태그6")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("태그의 개수는 최대 5개까지 가능합니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_중복된_태그_이름이_있으면_예외가_발생한다() throws Exception {
        // given
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 내용", null)),
                List.of(new RoadmapTagSaveRequest("태그1"), new RoadmapTagSaveRequest("태그1")));

        given(roadmapCreateService.create(any(), any()))
                .willThrow(new BadRequestException("태그 이름은 중복될 수 없습니다."));

        // expect
        로드맵_생성_요청(request, status().isBadRequest());
    }

    @Test
    void 로드맵의_리뷰를_생성한다() throws Exception {
        // given
        doNothing().when(roadmapCreateService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
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
        final String response = mockMvc.perform(post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
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
                .when(roadmapCreateService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.5);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
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
                .when(roadmapCreateService)
                .createReview(any(), any(), any());

        final String content = "a".repeat(1001);
        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest(content, 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
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
                .when(roadmapCreateService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
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
        doThrow(new BadRequestException("로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = 1L memberIdentifier = cokirikiri"))
                .when(roadmapCreateService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
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
                .when(roadmapCreateService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
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
                .when(roadmapCreateService)
                .createReview(any(), any(), any());

        final RoadmapReviewSaveRequest request = new RoadmapReviewSaveRequest("리뷰 내용", 5.0);
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        final String response = mockMvc.perform(post(API_PREFIX + "/roadmaps/{roadmapId}/reviews", 1L)
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

    @Test
    void 정상적으로_로드맵을_삭제한다() throws Exception {
        // given
        doNothing()
                .when(roadmapCreateService)
                .deleteRoadmap(anyString(), anyLong());

        // when
        mockMvc.perform(delete(API_PREFIX + "/roadmaps/{roadmapId}", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNoContent())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디").optional()))
                );
    }

    @Test
    void 로드맵_삭제시_존재하지_않는_로드맵인_경우_예외가_발생한다() throws Exception {
        // given
        doThrow(new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 1"))
                .when(roadmapCreateService)
                .deleteRoadmap(anyString(), anyLong());

        // when
        final String response = mockMvc.perform(delete(API_PREFIX + "/roadmaps/{roadmapId}", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.message")
                                .value("존재하지 않는 로드맵입니다. roadmapId = 1"))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디").optional()),
                        responseFields(
                                fieldWithPath("message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("존재하지 않는 로드맵입니다. roadmapId = 1");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_삭제시_자신이_생성한_로드맵이_아닌_경우_예외가_발생한다() throws Exception {
        // given
        doThrow(new ForbiddenException("해당 로드맵을 생성한 사용자가 아닙니다."))
                .when(roadmapCreateService)
                .deleteRoadmap(anyString(), anyLong());

        // when
        final String response = mockMvc.perform(delete(API_PREFIX + "/roadmaps/{roadmapId}", 1L)
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpectAll(status().isForbidden(),
                        jsonPath("$.message")
                                .value("해당 로드맵을 생성한 사용자가 아닙니다."))
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디").optional()),
                        responseFields(
                                fieldWithPath("message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("해당 로드맵을 생성한 사용자가 아닙니다.");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 정상적으로_로드맵_카테고리를_생성한다() throws Exception {
        // given
        final RoadmapCategorySaveRequest request = new RoadmapCategorySaveRequest("카테고리 이름");
        doNothing().when(roadmapCreateService)
                .createRoadmapCategory(request);

        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post(API_PREFIX + "/roadmaps/categories")
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isCreated())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("name").description("카테고리 이름")
                                        .attributes(new Attribute(RESTRICT, "- 길이 : 1 ~ 10")))));
    }

    @Test
    void 로드맵_카테고리_생성_시_카테고리_이름이_빈값일_경우() throws Exception {
        // given
        final RoadmapCategorySaveRequest request = new RoadmapCategorySaveRequest("");
        doNothing().when(roadmapCreateService)
                .createRoadmapCategory(request);

        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post(API_PREFIX + "/roadmaps/categories")
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("name").description("카테고리 이름")
                                        .attributes(new Attribute(RESTRICT, "- 길이 : 1 ~ 10")))));
    }

    @Test
    void 로드맵_카테고리_생성_시_카테고리_이름이_10자_초과일_경우() throws Exception {
        // given
        final RoadmapCategorySaveRequest request = new RoadmapCategorySaveRequest("10자가 초과되는 카테고리 이름입니다.");
        doThrow(new BadRequestException("카테고리 이름은 1자 이상 10자 이하입니다.")).when(roadmapCreateService)
                .createRoadmapCategory(request);

        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post(API_PREFIX + "/roadmaps/categories")
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isBadRequest())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("name").description("카테고리 이름")
                                        .attributes(new Attribute(RESTRICT, "- 길이 : 1 ~ 10"))),
                        responseFields(
                                fieldWithPath("message").description("예외 메시지"))));
    }

    @Test
    void 로드맵_카테고리_생성_시_카테고리_이름이_중복될_경우() throws Exception {
        // given
        final RoadmapCategorySaveRequest request = new RoadmapCategorySaveRequest("여행");
        doThrow(new ConflictException("이미 존재하는 이름의 카테고리입니다.")).when(roadmapCreateService)
                .createRoadmapCategory(request);

        final String jsonRequest = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post(API_PREFIX + "/roadmaps/categories")
                        .header(AUTHORIZATION, String.format(BEARER_TOKEN_FORMAT, "test-token"))
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isConflict())
                .andDo(documentationResultHandler.document(
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("액세스 토큰")),
                        requestFields(
                                fieldWithPath("name").description("카테고리 이름")
                                        .attributes(new Attribute(RESTRICT, "- 길이 : 1 ~ 10"))),
                        responseFields(
                                fieldWithPath("message").description("예외 메시지"))));
    }

    private RoadmapSaveRequest 로드맵_생성_요청을_생성한다(final Long categoryId, final String roadmapTitle,
                                               final String roadmapIntroduction, final String roadmapContent,
                                               final RoadmapDifficultyType roadmapDifficulty,
                                               final Integer requiredPeriod,
                                               final List<RoadmapNodeSaveRequest> roadmapNodesSaveRequests,
                                               final List<RoadmapTagSaveRequest> roadmapTagSaveRequests) {
        return new RoadmapSaveRequest(categoryId, roadmapTitle, roadmapIntroduction, roadmapContent, roadmapDifficulty,
                requiredPeriod, roadmapNodesSaveRequests, roadmapTagSaveRequests);
    }

    private void 로드맵_생성_요청(final RoadmapSaveRequest request, final ResultMatcher httpStatus) throws Exception {
        final String jsonRequest = objectMapper.writeValueAsString(request);
        final MockMultipartFile jsonDataFile = new MockMultipartFile("jsonData", "", "application/json",
                objectMapper.writeValueAsBytes(request));

        final List<RequestPartDescriptor> MULTIPART_FORM_데이터_설명_리스트 = new ArrayList<>();
        MULTIPART_FORM_데이터_설명_리스트.add(partWithName("jsonData").description("로드맵 생성 요청 json 데이터"));

        MockMultipartHttpServletRequestBuilder httpServletRequestBuilder = multipart(API_PREFIX + "/roadmaps");

        for (final RoadmapNodeSaveRequest roadmapNode : request.roadmapNodes()) {
            final String 로드맵_노드_제목 = roadmapNode.getTitle() != null ? roadmapNode.getTitle() : "name";
            final MockMultipartFile 가짜_이미지_객체 = new MockMultipartFile(로드맵_노드_제목,
                    "originalFileName.jpeg", "image/jpeg", "tempImage".getBytes());
            httpServletRequestBuilder = httpServletRequestBuilder.file(가짜_이미지_객체);

            MULTIPART_FORM_데이터_설명_리스트.add(
                    partWithName(로드맵_노드_제목).description("로드맵 노드 title")
            );
        }

        mockMvc.perform(httpServletRequestBuilder
                        .file(jsonDataFile)
                        .param("jsonData", jsonRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer accessToken")
                        .contextPath(API_PREFIX))
                .andExpect(httpStatus)
                .andDo(documentationResultHandler.document(
                        requestHeaders(headerWithName("Authorization").description("access token")),
                        requestParts(MULTIPART_FORM_데이터_설명_리스트),
                        requestPartFields("jsonData",
                                fieldWithPath("categoryId").description("로드맵 카테고리 아이디"),
                                fieldWithPath("title").description("로드맵 제목")
                                        .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 1-40")),
                                fieldWithPath("introduction").description("로드맵 소개글")
                                        .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 1-150")),
                                fieldWithPath("content").description("로드맵 본문 내용").optional()
                                        .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 0-2000")),
                                fieldWithPath("difficulty").description(
                                        "로드맵 난이도(VERY_EASY, EASY, NORMAL, DIFFICULT, VERY_DIFFICULT)"),
                                fieldWithPath("requiredPeriod").description("로드맵 전체 추천 소요 기간")
                                        .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 0-1000")),
                                fieldWithPath("roadmapNodes").description("로드맵 노드들"),
                                fieldWithPath("roadmapNodes[0].title").description("로드맵 노드의 제목")
                                        .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 1-40")),
                                fieldWithPath("roadmapNodes[0].content").description("로드맵 노드의 설명")
                                        .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 1-2000")),
                                fieldWithPath("roadmapNodes[0].images").ignored(),
                                fieldWithPath("roadmapTags[0].name").description("로드맵 태그 이름")
                                        .attributes(new Attributes.Attribute(RESTRICT, "- 길이 : 1-10"))
                                        .optional()
                        ),
                        responseHeaders(
                                headerWithName("Location").description("로드맵 아이디").optional()
                        )));
    }
}
