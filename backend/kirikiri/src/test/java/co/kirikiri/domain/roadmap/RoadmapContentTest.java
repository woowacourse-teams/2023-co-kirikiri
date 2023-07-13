package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.exception.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RoadmapContentTest {

    @Test
    void 로드맵_본문의_길이가_150보다_크면_예외가_발생한다() {
        final String content = "a".repeat(151);
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(new RoadmapNode("로드맵 노드 제목", "로드맵 노드 내용")));

        assertThatThrownBy(() -> new RoadmapContent(content, roadmapNodes))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_본문은_null값을_허용한다() {
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(new RoadmapNode("로드맵 노드 제목", "로드맵 노드 내용")));

        assertDoesNotThrow(() -> new RoadmapContent(null, roadmapNodes));
    }
}
