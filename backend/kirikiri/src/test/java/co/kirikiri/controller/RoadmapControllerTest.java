package co.kirikiri.controller;


import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.kirikiri.controller.helper.RestDocsHelper;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.RoadmapCategoryResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(RoadmapController.class)
class RoadmapControllerTest extends RestDocsHelper {

    @MockBean
    private RoadmapService roadmapService;

    @Test
    void 로드맵_카테고리_목록을_조회한다() throws Exception {
        // given
        when(roadmapService.getAllRoadmapCategories())
            .thenReturn(로드맵_카테고리_응답_리스트를_반환한다());

        // when, then
        mockMvc.perform(
                get("/api/roadmaps/categories")
                    .contextPath(API_PREFIX))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.[0].id").value(1L),
                jsonPath("$.[0].name").value("어학"),
                jsonPath("$.[1].id").value(2L),
                jsonPath("$.[1].name").value("IT"),
                jsonPath("$.[2].id").value(3L),
                jsonPath("$.[2].name").value("시험"),
                jsonPath("$.[3].id").value(4L),
                jsonPath("$.[3].name").value("운동"),
                jsonPath("$.[4].id").value(5L),
                jsonPath("$.[4].name").value("게임"),
                jsonPath("$.[5].id").value(6L),
                jsonPath("$.[5].name").value("음악"),
                jsonPath("$.[6].id").value(7L),
                jsonPath("$.[6].name").value("라이프"),
                jsonPath("$.[7].id").value(8L),
                jsonPath("$.[7].name").value("여가"),
                jsonPath("$.[8].id").value(9L),
                jsonPath("$.[8].name").value("기타"))
            .andDo(
                documentationResultHandler.document(
                    responseFields(
                        fieldWithPath("[0].id").description("카테고리 아이디"),
                        fieldWithPath("[0].name").description("카테고리 이름")
                    )
                )
            );
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
