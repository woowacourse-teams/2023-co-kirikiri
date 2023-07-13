package co.kirikiri.integration;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.integration.helper.IntegrationTest;
import co.kirikiri.persistence.RoadmapCategoryRepository;
import co.kirikiri.service.dto.RoadmapCategoryResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RoadmapIntegrationTest extends IntegrationTest {

    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public RoadmapIntegrationTest(final RoadmapCategoryRepository roadmapCategoryRepository) {
        this.roadmapCategoryRepository = roadmapCategoryRepository;
    }

    @Test
    void 로드맵_카테고리_리스트를_조회한다() {
        // given
        final List<RoadmapCategory> roadmapCategories = 로드맵_카테고리를_저장한다();

        // when
        final List<RoadmapCategoryResponse> roadmapCategoryResponses = given()
            .log().all()
            .when()
            .get("/api/roadmaps/categories")
            .then().log().all()
            .extract()
            .response()
            .as(new TypeRef<>() {
            });

        // then
        final List<RoadmapCategoryResponse> expected = 로드맵_카테고리_응답_리스트를_반환한다(roadmapCategories);

        assertThat(roadmapCategoryResponses)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private List<RoadmapCategory> 로드맵_카테고리를_저장한다() {
        final RoadmapCategory category1 = new RoadmapCategory("어학");
        final RoadmapCategory category2 = new RoadmapCategory("IT");
        final RoadmapCategory category3 = new RoadmapCategory("시험");
        final RoadmapCategory category4 = new RoadmapCategory("운동");
        final RoadmapCategory category5 = new RoadmapCategory("게임");
        final RoadmapCategory category6 = new RoadmapCategory("음악");
        final RoadmapCategory category7 = new RoadmapCategory("라이프");
        final RoadmapCategory category8 = new RoadmapCategory("여가");
        final RoadmapCategory category9 = new RoadmapCategory("기타");
        return roadmapCategoryRepository.saveAll(
            List.of(category1, category2, category3, category4, category5, category6, category7, category8, category9));
    }

    private List<RoadmapCategoryResponse> 로드맵_카테고리_응답_리스트를_반환한다(final List<RoadmapCategory> roadmapCategories) {
        final RoadmapCategoryResponse category1 = new RoadmapCategoryResponse(roadmapCategories.get(0).getId(), "어학");
        final RoadmapCategoryResponse category2 = new RoadmapCategoryResponse(roadmapCategories.get(1).getId(), "IT");
        final RoadmapCategoryResponse category3 = new RoadmapCategoryResponse(roadmapCategories.get(2).getId(), "시험");
        final RoadmapCategoryResponse category4 = new RoadmapCategoryResponse(roadmapCategories.get(3).getId(), "운동");
        final RoadmapCategoryResponse category5 = new RoadmapCategoryResponse(roadmapCategories.get(4).getId(), "게임");
        final RoadmapCategoryResponse category6 = new RoadmapCategoryResponse(roadmapCategories.get(5).getId(), "음악");
        final RoadmapCategoryResponse category7 = new RoadmapCategoryResponse(roadmapCategories.get(6).getId(), "라이프");
        final RoadmapCategoryResponse category8 = new RoadmapCategoryResponse(roadmapCategories.get(7).getId(), "여가");
        final RoadmapCategoryResponse category9 = new RoadmapCategoryResponse(roadmapCategories.get(8).getId(), "기타");
        return List.of(category1, category2, category3, category4, category5, category6, category7, category8,
            category9);
    }
}
