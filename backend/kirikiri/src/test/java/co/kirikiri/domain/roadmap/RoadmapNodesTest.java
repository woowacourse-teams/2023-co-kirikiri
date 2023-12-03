package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

class RoadmapNodesTest {

    @Test
    void 로드맵_노드를_추가한다() {
        // given
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(new RoadmapNode("로드맵 노드 제목1", "로드맵 노드 내용")));

        // when
        roadmapNodes.add(new RoadmapNode("로드맵 노드 제목2", "로드맵 노드 내용"));

        // then
        assertThat(roadmapNodes.getValues()).hasSize(2);
    }

    @Test
    void 로드맵_노드들의_로드맵_본문을_업데이트한다() {
        // given
        final RoadmapNodes roadmapNodes = new RoadmapNodes(
                List.of(new RoadmapNode("로드맵 노드 제목1", "로드맵 노드 내용1"), new RoadmapNode("로드맵 노드 제목2", "로드맵 노드 내용2")));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");

        // when
        roadmapNodes.updateAllRoadmapContent(roadmapContent);

        // then
        final List<RoadmapNode> nodes = roadmapNodes.getValues();
        assertAll(
                () -> assertThat(nodes.get(0).getRoadmapContent()).isEqualTo(roadmapContent),
                () -> assertThat(nodes.get(1).getRoadmapContent()).isEqualTo(roadmapContent)
        );
    }

}
