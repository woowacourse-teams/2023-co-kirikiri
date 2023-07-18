package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RoadmapContentsTest {

    @Test
    void 로드맵_본문을_추가한다() {
        // given
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(new RoadmapNode("로드맵 노드 제목", "로드맵 노드 내용")));
        final RoadmapContent roadmapContent = new RoadmapContent("로드맵 본문");
        roadmapContent.addNodes(roadmapNodes);
        final RoadmapContents roadmapContents = new RoadmapContents(List.of(roadmapContent));
        final RoadmapContent updatedRoadmapContent = new RoadmapContent("로드맵 본문 수정본");
        updatedRoadmapContent.addNodes(roadmapNodes);

        // when
        roadmapContents.add(updatedRoadmapContent);

        // then
        assertThat(roadmapContents.getContents()).usingRecursiveComparison()
                .isEqualTo(List.of(roadmapContent, updatedRoadmapContent));
    }
}
