package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapDifficultyType;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@WebMvcTest(RoadmapController.class)
class RoadmapControllerTest extends ControllerTestHelper {

    @MockBean
    private RoadmapService roadmapService;

    @Test
    void 정상적으로_로드맵을_생성한다() throws Exception {
        // given
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willReturn(1L);

        // expect
        로드맵_생성_요청(jsonRequest, status().isCreated());
    }

    @Test
    void 로드맵_생성시_유효하지_않은_카테고리_아이디를_입력하면_예외가_발생한다() throws Exception {
        // given
        final Long categoryId = 10L;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(categoryId, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new NotFoundException("존재하지 않는 카테고리입니다. categoryId = 10"));

        // expect
        로드맵_생성_요청(jsonRequest, status().isNotFound());
    }

    @Test
    void 로드맵_생성시_카테고리_아이디를_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final Long categoryId = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(categoryId, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_제목의_길이가_40보다_크면_예외가_발생한다() throws Exception {
        // given
        final String title = "a".repeat(41);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, title, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 제목의 길이는 최소 1글자, 최대 40글자입니다."));

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_제목을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String title = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, title, "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_소개글의_길이가_150보다_크면_예외가_발생한다() throws Exception {
        // given
        final String introduction = "a".repeat(151);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", introduction, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 소개글의 길이는 최소 1글자, 최대 150글자입니다."));

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_소개글을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String introduction = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", introduction, "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_본문의_길이가_150보다_크면_예외가_발생한다() throws Exception {
        // given
        final String content = "a".repeat(151);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", content,
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 본문의 길이는 최대 150글자 입니다."));

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_난이도를_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final RoadmapDifficultyType difficulty = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                difficulty, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_추천_소요기간이_0보다_작으면_예외가_발생한다() throws Exception {
        // given
        final Integer requiredPeriod = -1;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, requiredPeriod,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 추천 소요 기간은 최소 0일, 최대 1000일입니다."));

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_추천_소요기간이_1000보다_크면_예외가_발생한다() throws Exception {
        // given
        final Integer requiredPeriod = 1001;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, requiredPeriod,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 추천 소요 기간은 최소 0일, 최대 1000일입니다."));

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_추천_소요기간을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final Integer requiredPeriod = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, requiredPeriod,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_제목의_길이가_40보다_크면_예외가_발생한다() throws Exception {
        // given
        final String nodeTitle = "a".repeat(41);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest(nodeTitle, "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 노드의 제목의 길이는 최소 1글자, 최대 40글자입니다."));

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_제목을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String nodeTitle = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest(nodeTitle, "로드맵 1주차에는 알고리즘을 배울거에요.")));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_설명의_길이가_200보다_크면_예외가_발생한다() throws Exception {
        // given
        final String nodeContent = "a".repeat(201);
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", nodeContent)));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        given(roadmapService.create(any(), any()))
                .willThrow(new BadRequestException("로드맵 노드의 설명의 길이는 최소 1글자, 최대 200글자입니다."));

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    @Test
    void 로드맵_생성시_로드맵_노드의_설명을_입력하지_않으면_예외가_발생한다() throws Exception {
        // given
        final String nodeContent = null;
        final RoadmapSaveRequest request = 로드맵_생성_요청을_생성한다(1L, "로드맵 제목", "로드맵 소개글", "로드맵 본문",
                RoadmapDifficultyType.DIFFICULT, 30,
                List.of(new RoadmapNodeSaveRequest("로드맵 1주차", nodeContent)));
        final String jsonRequest = objectMapper.writeValueAsString(request);

        // expect
        로드맵_생성_요청(jsonRequest, status().isBadRequest());
    }

    private RoadmapSaveRequest 로드맵_생성_요청을_생성한다(final Long categoryId, final String roadmapTitle,
                                               final String roadmapIntroduction, final String roadmapContent,
                                               final RoadmapDifficultyType roadmapDifficulty,
                                               final Integer requiredPeriod,
                                               final List<RoadmapNodeSaveRequest> roadmapNodesSaveRequests) {
        return new RoadmapSaveRequest(categoryId, roadmapTitle, roadmapIntroduction, roadmapContent, roadmapDifficulty,
                requiredPeriod, roadmapNodesSaveRequests);
    }

    private void 로드맵_생성_요청(final String jsonRequest, final ResultMatcher httpStatus) throws Exception {
        mockMvc.perform(post(API_PREFIX + "/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequest)
                        .contextPath(API_PREFIX))
                .andExpect(httpStatus)
                .andDo(documentationResultHandler.document(
                        requestFields(
                                fieldWithPath("categoryId").description("로드맵 카테고리 아이디"),
                                fieldWithPath("title").description("로드맵 제목")
                                        .attributes(new Attributes.Attribute("range", "1-40")),
                                fieldWithPath("introduction").description("로드맵 소개글")
                                        .attributes(new Attributes.Attribute("range", "1-150")),
                                fieldWithPath("content").description("로드맵 본문 내용").optional()
                                        .attributes(new Attributes.Attribute("range", "0-150")),
                                fieldWithPath("difficulty").description(
                                        "로드맵 난이도(VERY_EASY, EASY, NORMAL, DIFFICULT, VERY_DIFFICULT)"),
                                fieldWithPath("requiredPeriod").description("로드맵 전체 추천 소요 기간")
                                        .attributes(new Attributes.Attribute("range", "0-1000")),
                                fieldWithPath("roadmapNodes").description("로드맵 노드들"),
                                fieldWithPath("roadmapNodes[0].title").description("로드맵 노드의 제목")
                                        .attributes(new Attributes.Attribute("range", "1-40")),
                                fieldWithPath("roadmapNodes[0].content").description("로드맵 노드의 설명")
                                        .attributes(new Attributes.Attribute("range", "1-200"))
                        ),
                        responseHeaders(
                                headerWithName("Location").description("로드맵 아이디").optional()
                        )));
    }

    @Test
    void 로드맵_목록을_조건에_따라_조회한다() throws Exception {
        // given
        final PageResponse<RoadmapResponse> 로드맵_페이지_응답 = 로드맵_페이지_응답을_생성한다();
        when(roadmapService.findRoadmapsByFilterType(any(), any(), any()))
                .thenReturn(로드맵_페이지_응답);

        // when
        final String 응답값 = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps")
                                .param("categoryId", "1")
                                .param("filterCond", RoadmapFilterTypeDto.LATEST.name())
                                .param("page", "1")
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(
                        documentationResultHandler.document(
                                queryParameters(
                                        parameterWithName("categoryId").description("카테고리 아이디(미전송 시 전체 조회)")
                                                .optional(),
                                        parameterWithName("filterCond").description(
                                                        "필터 조건(GOAL_ROOM_COUNT, LATEST, PARTICIPANT_COUNT)")
                                                .optional(),
                                        parameterWithName("page").description("타겟 페이지 (1부터 시작)"),
                                        parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                                responseFields(
                                        fieldWithPath("currentPage").description("현재 페이지 값"),
                                        fieldWithPath("totalPage").description("총 페이지 수"),
                                        fieldWithPath("data[0].roadmapId").description("로드맵 아이디"),
                                        fieldWithPath("data[0].roadmapTitle").description("로드맵 제목"),
                                        fieldWithPath("data[0].introduction").description("로드맵 소개글"),
                                        fieldWithPath("data[0].difficulty").description("로드맵 난이도"),
                                        fieldWithPath("data[0].recommendedRoadmapPeriod").description("로드맵 추천 기간"),
                                        fieldWithPath("data[0].creator.id").description("로드맵 크리에이터 아이디"),
                                        fieldWithPath("data[0].creator.name").description("로드맵 크리에이터 이름"),
                                        fieldWithPath("data[0].category.id").description("로드맵 카테고리 아이디"),
                                        fieldWithPath("data[0].category.name").description("로드맵 카테고리 이름"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final PageResponse<RoadmapResponse> 응답값으로_생성한_로드맵_페이지 = objectMapper.readValue(응답값,
                new TypeReference<>() {
                });

        final PageResponse<RoadmapResponse> 예상되는_로드맵_페이지_응답 = 로드맵_페이지_응답을_생성한다();
        assertThat(응답값으로_생성한_로드맵_페이지)
                .usingRecursiveComparison()
                .isEqualTo(예상되는_로드맵_페이지_응답);
    }

    @Test
    void 로드맵_목록_조회시_유효하지_않은_카테고리_아이디를_보내면_예외가_발생한다() throws Exception {
        // given
        when(roadmapService.findRoadmapsByFilterType(any(), any(), any())).thenThrow(
                new NotFoundException("존재하지 않는 카테고리입니다. categoryId = 1L"));

        // when, then
        mockMvc.perform(
                        get(API_PREFIX + "/roadmaps")
                                .param("categoryId", "1")
                                .param("filterCond", RoadmapFilterTypeDto.LATEST.name())
                                .param("page", "1")
                                .param("size", "10")
                                .contextPath(API_PREFIX))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.message").value("존재하지 않는 카테고리입니다. categoryId = 1L"))
                .andDo(documentationResultHandler.document(
                        queryParameters(
                                parameterWithName("categoryId").description("잘못된 카테고리 아이디"),
                                parameterWithName("filterCond").description(
                                                "필터 조건(GOAL_ROOM_COUNT, LATEST, PARTICIPANT_COUNT)")
                                        .optional(),
                                parameterWithName("page").description("타겟 페이지 (1부터 시작)"),
                                parameterWithName("size").description("한 페이지에서 받아올 로드맵의 수")),
                        responseFields(fieldWithPath("message").description("예외 메시지"))));
    }

    @Test
    void 로드맵_카테고리_목록을_조회한다() throws Exception {
        // given
        final List<RoadmapCategoryResponse> expected = 로드맵_카테고리_응답_리스트를_반환한다();
        when(roadmapService.getAllRoadmapCategories())
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get("/api/roadmaps/categories")
                                .contextPath(API_PREFIX))
                .andDo(
                        documentationResultHandler.document(
                                responseFields(
                                        fieldWithPath("[0].id").description("카테고리 아이디"),
                                        fieldWithPath("[0].name").description("카테고리 이름")
                                )
                        )
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = objectMapper.readValue(response,
                new TypeReference<>() {
                });

        assertThat(roadmapCategoryResponses)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private PageResponse<RoadmapResponse> 로드맵_페이지_응답을_생성한다() {
        final RoadmapResponse roadmapResponse1 = new RoadmapResponse(1L, "로드맵 제목1", "로드맵 소개글1", "NORMAL", 10,
                new MemberResponse(1L, "코끼리"), new RoadmapCategoryResponse(1L, "여행"));
        final RoadmapResponse roadmapResponse2 = new RoadmapResponse(2L, "로드맵 제목2", "로드맵 소개글2", "DIFFICULT", 7,
                new MemberResponse(2L, "끼리코"), new RoadmapCategoryResponse(2L, "IT"));
        return new PageResponse<>(1, 2, List.of(roadmapResponse1, roadmapResponse2));
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다() {
        final RoadmapCategoryResponse category1 = new RoadmapCategoryResponse(1L, "어학");
        final RoadmapCategoryResponse category2 = new RoadmapCategoryResponse(2L, "IT");
        final RoadmapCategoryResponse category3 = new RoadmapCategoryResponse(3L, "시험");
        final RoadmapCategoryResponse category4 = new RoadmapCategoryResponse(4L, "운동");
        final RoadmapCategoryResponse category5 = new RoadmapCategoryResponse(5L, "게임");
        final RoadmapCategoryResponse category6 = new RoadmapCategoryResponse(6L, "음악");
        final RoadmapCategoryResponse category7 = new RoadmapCategoryResponse(7L, "라이프");
        final RoadmapCategoryResponse category8 = new RoadmapCategoryResponse(8L, "여가");
        final RoadmapCategoryResponse category9 = new RoadmapCategoryResponse(9L, "기타");
        return List.of(category1, category2, category3, category4, category5, category6, category7, category8,
                category9);
    }

    @Test
    void 단일_로드맵_정보를_조회한다() throws Exception {
        //given
        final RoadmapResponse expectedResponse = 단일_로드맵_조회에_대한_응답();
        when(roadmapService.findRoadmap(anyLong())).thenReturn(expectedResponse);

        //when
        final MvcResult response = mockMvc.perform(get(API_PREFIX + "/roadmaps/1")
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(documentationResultHandler.document(
                        responseFields(
                                fieldWithPath("roadmapId").description("로드맵 아이디"),
                                fieldWithPath("category.id").description("로드맵 카테고리 아이디"),
                                fieldWithPath("category.name").description("로드맵 카테고리 이름"),
                                fieldWithPath("title").description("로드맵 제목"),
                                fieldWithPath("introduction").description("로드맵 소개글"),
                                fieldWithPath("creator.id").description("로드맵 크리에이터 아이디"),
                                fieldWithPath("creator.nickname").description("로드맵 크리에이터 닉네임"),
                                fieldWithPath("content").description("로드맵 본문"),
                                fieldWithPath("difficulty").description("로드맵 난이도"),
                                fieldWithPath("recommendedRoadmapPeriod").description("로드맵 추천 기간"),
                                fieldWithPath("nodes[0].title").description("로드맵 노드 제목"),
                                fieldWithPath("nodes[0].description").description("로드맵 노드 본문"),
                                fieldWithPath("nodes[0].imageUrls[0]").description("로드맵 노드 이미지 파일 경로")
                        )))
                .andReturn();

        //then
        final RoadmapResponse roadmapResponse = jsonToClass(response, new TypeReference<>() {
        });

        assertThat(roadmapResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    void 존재하지_않는_로드맵_아이디로_요청_시_예외를_반환한다() throws Exception {
        // given
        when(roadmapService.findRoadmap(anyLong())).thenThrow(
                new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 2"));

        // when
        // then
        mockMvc.perform(get(API_PREFIX + "/roadmaps/2")
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 로드맵입니다. roadmapId = 2"))
                .andDo(documentationResultHandler.document(
                        responseFields(
                                fieldWithPath("message").description("예외 메세지")
                        )));
    }

    private RoadmapResponse 단일_로드맵_조회에_대한_응답() {
        final RoadmapCategoryResponse category = new RoadmapCategoryResponse(1, "운동");
        final MemberResponse creator = new MemberResponse(1, "닉네임");
        final List<RoadmapNodeResponse> nodes = List.of(
                new RoadmapNodeResponse("1번 노드", "1번 노드 설명", List.of("image1-filepath", "image2-filepath")),
                new RoadmapNodeResponse("2번 노드", "2번 노드 설명", Collections.emptyList())
        );
        return new RoadmapResponse(1L, category, "제목", "소개글", creator, "본문",
                "EASY", 100, nodes);
    }
}
