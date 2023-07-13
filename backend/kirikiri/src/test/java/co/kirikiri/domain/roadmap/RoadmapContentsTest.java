package co.kirikiri.domain.roadmap;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.exception.BadRequestException;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RoadmapContentsTest {

    @Test
    void 로드맵의_본문이_1개_보다_적으면_예외가_발생한다() {
        assertThatThrownBy(() -> new RoadmapContents(emptyList()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 로드맵_본문을_추가한다() {
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(new RoadmapNode("로드맵 노드 제목", "로드맵 노드 내용")));
        final RoadmapContents roadmapContents = new RoadmapContents(
                List.of(new RoadmapContent("로드맵 본문", roadmapNodes)));
        roadmapContents.add(new RoadmapContent("로드맵 본문 수정본", roadmapNodes));

        assertThat(roadmapContents.getContents()).hasSize(2);
    }
}
