package co.kirikiri.roadmap.domain;

import co.kirikiri.roadmap.domain.exception.RoadmapException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

import static co.kirikiri.roadmap.domain.RoadmapDifficulty.DIFFICULT;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RoadmapTest {

    private final Long creatorId = 1L;
    private final RoadmapCategory category = 카테고리를_생성한다();
    private final RoadmapTags emptyTags = new RoadmapTags(new ArrayList<>());

    @Test
    void 로드맵이_성공적으로_생성된다() {
        // expect
        assertDoesNotThrow(() -> new Roadmap("로드맵 제목", "로드맵 소개글", 30, DIFFICULT,
                creatorId, category, null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 41})
    void 로드맵_제목의_길이가_1보다_작거나_40보다_크면_예외가_발생한다(final int titleLength) {
        // given
        final String title = "a".repeat(titleLength);

        // expect
        assertThatThrownBy(() -> new Roadmap(title, "로드맵 소개글", 30, DIFFICULT, creatorId, category, emptyTags))
                .isInstanceOf(RoadmapException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 151})
    void 로드맵_소개글의_길이가_1보다_작거나_150보다_크면_예외가_발생한다(final int introductionLength) {
        // given
        final String introduction = "a".repeat(introductionLength);

        // expect
        assertThatThrownBy(() -> new Roadmap("로드맵 제목", introduction, 30, DIFFICULT, creatorId, category, emptyTags))
                .isInstanceOf(RoadmapException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1001})
    void 로드맵_추천_소요_기간이_0보다_작고_1000보다_크면_예외가_발생한다(final int requiredPeriod) {
        // expect
        assertThatThrownBy(() -> new Roadmap("로드맵 제목", "로드맵 소개글", requiredPeriod, DIFFICULT, creatorId, category, emptyTags))
                .isInstanceOf(RoadmapException.class);
    }

    private RoadmapCategory 카테고리를_생성한다() {
        return new RoadmapCategory(1L, "여가");
    }
}
