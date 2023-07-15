package co.kirikiri.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.RestDocsHelper;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapDetailResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.SingleRoadmapResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(RoadmapController.class)
class RoadmapControllerTest extends RestDocsHelper {

    @MockBean
    private RoadmapService roadmapService;

    @Test
    void 단일_로드맵_정보를_조회한다() throws Exception {
        //given
        final SingleRoadmapResponse expectedResponse = 단일_로드맵_조회에_대한_응답();
        when(roadmapService.findSingleRoadmap(anyLong())).thenReturn(expectedResponse);

        //when
        final String response = mockMvc.perform(get(API_PREFIX + "/roadmaps/1")
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isOk())
                .andDo(documentationResultHandler.document(
                        responseFields(
                                fieldWithPath("data.category.id").description("로드맵 카테고리 아이디"),
                                fieldWithPath("data.category.name").description("로드맵 카테고리 이름"),
                                fieldWithPath("data.title").description("로드맵 제목"),
                                fieldWithPath("data.introduction").description("로드맵 소개글"),
                                fieldWithPath("data.creator.id").description("로드맵 크리에이터 아이디"),
                                fieldWithPath("data.creator.nickname").description("로드맵 크리에이터 닉네임"),
                                fieldWithPath("data.content").description("로드맵 본문"),
                                fieldWithPath("data.difficulty").description("로드맵 난이도"),
                                fieldWithPath("data.recommendedRoadmapPeriod").description("로드맵 추천 기간"),
                                fieldWithPath("data.nodes[0].title").description("로드맵 노드 제목"),
                                fieldWithPath("data.nodes[0].description").description("로드맵 노드 본문"),
                                fieldWithPath("data.nodes[0].imageUrls[0]").description("로드맵 노드 이미지 파일 경로")
                        )))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //then
        final SingleRoadmapResponse singleRoadmapResponse = objectMapper.readValue(response,
                new TypeReference<>() {
                });

        assertThat(singleRoadmapResponse)
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    void 존재하지_않는_로드맵_아이디로_요청_시_예외를_반환한다() throws Exception {
        // given
        when(roadmapService.findSingleRoadmap(anyLong())).thenThrow(
                new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = 2"));

        // when
        // then
        mockMvc.perform(get(API_PREFIX + "/roadmaps/2")
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .contextPath(API_PREFIX))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 로드맵입니다. roadmapId = 2"));
    }

    private SingleRoadmapResponse 단일_로드맵_조회에_대한_응답() {
        final RoadmapCategoryResponse category = new RoadmapCategoryResponse(1, "운동");
        final MemberResponse creator = new MemberResponse(1, "닉네임");
        final List<RoadmapNodeResponse> nodes = List.of(
                new RoadmapNodeResponse("1번 노드", "1번 노드 설명", List.of("image1-filepath", "image2-filepath")),
                new RoadmapNodeResponse("2번 노드", "2번 노드 설명", Collections.emptyList())
        );
        final RoadmapDetailResponse roadmapDetail = new RoadmapDetailResponse(category, "제목", "소개글", creator, "본문",
                "EASY", 100, nodes);

        return new SingleRoadmapResponse(roadmapDetail);
    }
}