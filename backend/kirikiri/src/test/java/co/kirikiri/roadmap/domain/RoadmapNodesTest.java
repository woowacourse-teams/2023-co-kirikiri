package co.kirikiri.roadmap.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import co.kirikiri.roadmap.domain.exception.RoadmapException;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

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

    @Test
    void 로드맵_노드_아이디로_노드를_반환한다() {
        // given
        final RoadmapNode node1 = new RoadmapNode(1L, "title1", "content1");
        final RoadmapNode node2 = new RoadmapNode(2L, "title2", "content2");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(node1, node2));

        // when
        final long findNodeId = 1;
        final long notExistId = 3;
        final Optional<RoadmapNode> foundNode1 = roadmapNodes.findById(findNodeId);
        final Optional<RoadmapNode> foundNode2 = roadmapNodes.findById(notExistId);

        // then
        assertAll(
                () -> assertThat(node1).isEqualTo(foundNode1.get()),
                () -> assertThat(foundNode2).isEmpty()
        );
    }

    @Test
    void 로드맵_노드_제목으로_노드를_반환한다() {
        // given
        final RoadmapNode node1 = new RoadmapNode(1L, "title1", "content1");
        final RoadmapNode node2 = new RoadmapNode(2L, "title2", "content2");
        final RoadmapNodes roadmapNodes = new RoadmapNodes(List.of(node1, node2));

        // when
        final String findNodeTitle = "title1";
        final String notExistTitle = "nothing";
        final Optional<RoadmapNode> foundNode1 = roadmapNodes.findByTitle(findNodeTitle);
        final Optional<RoadmapNode> foundNode2 = roadmapNodes.findByTitle(notExistTitle);

        // then
        assertAll(
                () -> assertThat(node1).isEqualTo(foundNode1.get()),
                () -> assertThat(foundNode2).isEmpty()
        );
    }

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
