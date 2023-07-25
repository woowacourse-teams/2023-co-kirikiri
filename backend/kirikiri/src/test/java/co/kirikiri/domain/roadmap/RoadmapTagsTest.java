package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoadmapTagsTest {

    @Test
    void 로드맵_태그의_수가_5개_이하면_정상적으로_생성된다() {
        // given
        final List<RoadmapTag> values = List.of(
                new RoadmapTag("태그1"),
                new RoadmapTag("태그2"),
                new RoadmapTag("태그3"),
                new RoadmapTag("태그4"),
                new RoadmapTag("태그5"));

        // when
        final RoadmapTags roadmapTags = assertDoesNotThrow(() -> new RoadmapTags(values));

        // then
        assertThat(roadmapTags)
                .isInstanceOf(RoadmapTags.class);
    }

    @Test
    void 로드맵_태그의_수가_5개_초과면_예외가_발생한다() {
        // given
        final List<RoadmapTag> values = List.of(
                new RoadmapTag("태그1"),
                new RoadmapTag("태그2"),
                new RoadmapTag("태그3"),
                new RoadmapTag("태그4"),
                new RoadmapTag("태그5"),
                new RoadmapTag("태그6"));

        // expected
        assertThatThrownBy(() -> new RoadmapTags(values))
                .isInstanceOf(BadRequestException.class);
    }
}
