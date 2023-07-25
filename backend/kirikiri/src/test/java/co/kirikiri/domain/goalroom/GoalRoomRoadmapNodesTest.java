package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.roadmap.RoadmapNode;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomRoadmapNodesTest {

    @Test
    void 노드의_총_기간을_더한다() {
        // given
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = 골룸_노드를_생성한다();

        // when
        final int totalPeriod = goalRoomRoadmapNodes.addTotalPeriod();

        // then
        assertThat(totalPeriod)
                .isSameAs(17);
    }

    private GoalRoomRoadmapNodes 골룸_노드를_생성한다() {
        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                LocalDate.of(2023, 7, 19),
                LocalDate.of(2023, 7, 30), 10, new RoadmapNode("로드맵 제목 1", "로드맵 내용 1"));

        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                LocalDate.of(2023, 8, 1),
                LocalDate.of(2023, 8, 5), 10, new RoadmapNode("로드맵 제목 2", "로드맵 내용 2"));

        return new GoalRoomRoadmapNodes(
                List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode));
    }
}
