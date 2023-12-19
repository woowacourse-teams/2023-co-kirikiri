package co.kirikiri.domain.roadmap;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapContents;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import org.junit.jupiter.api.Test;
import java.util.List;

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
        assertThat(roadmapContents.getValues()).usingRecursiveComparison()
                .isEqualTo(List.of(roadmapContent, updatedRoadmapContent));
    }
}
