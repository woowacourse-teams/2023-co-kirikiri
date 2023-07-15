package co.kirikiri.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import co.kirikiri.controller.helper.RestDocsHelper;
import co.kirikiri.service.RoadmapService;
import co.kirikiri.service.dto.RoadmapCategoryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
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
