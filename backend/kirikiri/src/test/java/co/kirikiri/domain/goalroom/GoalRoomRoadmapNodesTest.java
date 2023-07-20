package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomRoadmapNodesTest {

    @Test
    void 골룸_로드맵_노드들_중_첫번째_시작날짜를_구한다() {
        // given
        // when
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 2), LocalDate.of(2023, 7, 9), null),
                new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 10), LocalDate.of(2023, 7, 20), null)
        ));

        // expect
        assertThat(goalRoomRoadmapNodes.getGoalRoomStartDate()).isEqualTo(LocalDate.of(2023, 7, 2));
    }

    @Test
    void 골룸_로드맵_노드들_중_마지막_종료날짜를_구한다() {
        // given
        // when
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 2), LocalDate.of(2023, 7, 9), null),
                new GoalRoomRoadmapNode(LocalDate.of(2023, 7, 10), LocalDate.of(2023, 7, 20), null)
        ));

        // then
        assertThat(goalRoomRoadmapNodes.getGoalRoomEndDate()).isEqualTo(LocalDate.of(2023, 7, 20));
    }
}
