package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.domain.roadmap.exception.RoadmapException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RoadmapNodeTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 41})
    void 로드맵_노드의_제목의_길이가_1보다_작거나_40보다_크면_예외가_발생한다(final int titleLength) {
        // given
        final String title = "a".repeat(titleLength);

        // expect
        assertThatThrownBy(() -> new RoadmapNode(title, "로드맵 설명"))
                .isInstanceOf(RoadmapException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2001})
    void 로드맵_노드의_설명의_길이가_1보다_작거나_2000보다_크면_예외가_발생한다(final int contentLength) {
        // given
        final String content = "a".repeat(contentLength);

        // expect
        assertThatThrownBy(() -> new RoadmapNode("로드맵 제목", content))
                .isInstanceOf(RoadmapException.class);
    }
}
