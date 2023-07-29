package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.ControllerTestHelper;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.RoadmapCreateService;
import co.kirikiri.service.RoadmapReadService;
import co.kirikiri.service.dto.ErrorResponse;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(RoadmapController.class)
class RoadmapReadApiTest extends ControllerTestHelper {

    @MockBean
    private RoadmapReadService roadmapReadService;

    @MockBean
    private RoadmapCreateService roadmapCreateService;

    @Test
    void 단일_로드맵_정보를_조회한다() throws Exception {
        //given
        final RoadmapResponse expectedResponse = 단일_로드맵_조회에_대한_응답();
        when(roadmapReadService.findRoadmap(anyLong())).thenReturn(expectedResponse);

        //when
        final MvcResult response = mockMvc.perform(get(API_PREFIX + "/roadmaps/{roadmapId}", 1L)
                        .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디")
                        ),
                        responseFields(
                                fieldWithPath("roadmapId").description("로드맵 아이디"),
                                fieldWithPath("category.id").description("로드맵 카테고리 아이디"),
                                fieldWithPath("category.name").description("로드맵 카테고리 이름"),
                                fieldWithPath("roadmapTitle").description("로드맵 제목"),
                                fieldWithPath("introduction").description("로드맵 소개글"),
                                fieldWithPath("creator.id").description("로드맵 크리에이터 아이디"),
                                fieldWithPath("creator.name").description("로드맵 크리에이터 닉네임"),
                                fieldWithPath("content.id").description("로드맵 컨텐츠 아이디"),
                                fieldWithPath("content.content").description("로드맵 컨텐츠 본문"),
                                fieldWithPath("content.nodes[0].id").description("로드맵 노드 아이디"),
                                fieldWithPath("content.nodes[0].title").description("로드맵 노드 제목"),
                                fieldWithPath("content.nodes[0].description").description("로드맵 노드 본문"),
                                fieldWithPath("content.nodes[0].imageUrls[0]").description("로드맵 노드 이미지 파일 경로"),
                                fieldWithPath("difficulty").description("로드맵 난이도"),
                                fieldWithPath("recommendedRoadmapPeriod").description("로드맵 추천 기간")
                        )))
                .andReturn();

        //then
        final RoadmapResponse roadmapResponse = jsonToClass(response, new TypeReference<>() {
        });

        assertThat(roadmapResponse)
                .isEqualTo(expectedResponse);
    }

    @Test
    void 존재하지_않는_로드맵_아이디로_요청_시_예외를_반환한다() throws Exception {
        // given
        when(roadmapReadService.findRoadmap(anyLong())).thenThrow(
                new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 1"));

        // when
        // then
        mockMvc.perform(get(API_PREFIX + "/roadmaps/{roadmapId}", 1L)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 로드맵입니다. roadmapId = 1"))
                .andDo(documentationResultHandler.document(
                        pathParameters(
                                parameterWithName("roadmapId").description("로드맵 아이디")),
                        responseFields(
                                fieldWithPath("message").description("예외 메세지")
                        )));
    }

    @Test
    void 로드맵_목록을_조건에_따라_조회한다() throws Exception {
        // given
        final PageResponse<RoadmapResponse> expected = 로드맵_페이지_응답을_생성한다();
        when(roadmapReadService.findRoadmapsByFilterType(any(), any(), any()))
                .thenReturn(expected);

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps")
                                .param("categoryId", "1")
                                .param("filterCond", RoadmapFilterTypeRequest.LATEST.name())
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
        final PageResponse<RoadmapResponse> roadmapPageResponse = objectMapper.readValue(response,
                new TypeReference<>() {
                });
        assertThat(roadmapPageResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_목록_조회시_유효하지_않은_카테고리_아이디를_보내면_예외가_발생한다() throws Exception {
        // given
        when(roadmapReadService.findRoadmapsByFilterType(any(), any(), any())).thenThrow(
                new NotFoundException("존재하지 않는 카테고리입니다. categoryId = 1L"));

        // when
        final String response = mockMvc.perform(
                        get(API_PREFIX + "/roadmaps")
                                .param("categoryId", "1")
                                .param("filterCond", RoadmapFilterTypeRequest.LATEST.name())
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
                        responseFields(fieldWithPath("message").description("예외 메시지"))))
                .andReturn().getResponse()
                .getContentAsString();

        // then
        final ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        final ErrorResponse expected = new ErrorResponse("존재하지 않는 카테고리입니다. categoryId = 1L");
        assertThat(errorResponse)
                .isEqualTo(expected);
    }

    @Test
    void 로드맵_카테고리_목록을_조회한다() throws Exception {
        // given
        final List<RoadmapCategoryResponse> expected = 로드맵_카테고리_응답_리스트를_반환한다();
        when(roadmapReadService.findAllRoadmapCategories())
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
                .isEqualTo(expected);
    }

    private RoadmapResponse 단일_로드맵_조회에_대한_응답() {
        final RoadmapCategoryResponse category = new RoadmapCategoryResponse(1, "운동");
        final MemberResponse creator = new MemberResponse(1, "닉네임");
        final List<RoadmapNodeResponse> nodes = List.of(
                new RoadmapNodeResponse(1L, "1번 노드", "1번 노드 설명", List.of("image1-filepath", "image2-filepath")),
                new RoadmapNodeResponse(2L, "2번 노드", "2번 노드 설명", Collections.emptyList())
        );
        return new RoadmapResponse(1L, category, "제목", "소개글", creator,
                new RoadmapContentResponse(1L, "본문", nodes), "EASY", 100);
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
}