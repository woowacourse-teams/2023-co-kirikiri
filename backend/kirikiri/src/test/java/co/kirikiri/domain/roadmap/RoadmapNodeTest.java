package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class RoadmapNodeTest {

    @Test
    void 로드맵_노드의_제목의_길이가_1보다_작으면_예외가_발생한다() {
        // given
        final String title = "";

        // expect
        assertThatThrownBy(() -> new RoadmapNode(title, "로드맵 설명"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_노드의_제목의_길이가_40보다_크면_예외가_발생한다() {
        // given
        final String title = "a".repeat(41);

        // expect
        assertThatThrownBy(() -> new RoadmapNode(title, "로드맵 설명"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_노드의_설명의_길이가_1보다_작으면_예외가_발생한다() {
        // given
        final String content = "";

        // expect
        assertThatThrownBy(() -> new RoadmapNode("로드맵 제목", content))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_노드의_설명의_길이가_200보다_크면_예외가_발생한다() {
        // given
        final String content = "a".repeat(201);

        // expect
        assertThatThrownBy(() -> new RoadmapNode("로드맵 제목", content))
                .isInstanceOf(BadRequestException.class);
    }
}
