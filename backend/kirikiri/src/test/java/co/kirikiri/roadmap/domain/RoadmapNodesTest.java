package co.kirikiri.roadmap.domain;

import co.kirikiri.roadmap.domain.exception.RoadmapException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoadmapNodesTest {

    @Test
    void 로드맵_본문의_노드의_이름이_겹치면_예외를_던진다() {
        // given
        final String title = "title";
        final RoadmapNode node1 = new RoadmapNode(title, "content1");
        final RoadmapNode node2 = new RoadmapNode(title, "content1");

        // when,then
        assertThatThrownBy(() -> new RoadmapNodes(List.of(node1, node2)))
                .isInstanceOf(RoadmapException.class)
                .hasMessage("한 로드맵에 같은 이름의 노드가 존재할 수 없습니다.");
    }

    // todo: RoadmapNodes 도메인 테스트 추가하기

    @Test
    void 로드맵_노드를_추가한다() {
        // given
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(new RoadmapNode("로드맵 노드 제목1", "로드맵 노드 내용")));

        // when
        roadmapNodes.add(new RoadmapNode("로드맵 노드 제목2", "로드맵 노드 내용"));

        // then
        assertThat(roadmapNodes.getValues()).hasSize(2);
    }
}
