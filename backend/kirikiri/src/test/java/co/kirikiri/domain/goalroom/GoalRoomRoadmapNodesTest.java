package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.exception.BadRequestException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GoalRoomRoadmapNodesTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate TEN_DAY_LATER = TODAY.plusDays(10);
    private static final LocalDate TWENTY_DAY_LAYER = TODAY.plusDays(20);
    private static final LocalDate THIRTY_DAY_LATER = TODAY.plusDays(30);

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 100})
    void 정상적으로_골룸_노드들을_생성한다(final long daysToAdd) {
        //given
        final LocalDate firstStartDate = LocalDate.now();
        final LocalDate firstEndDate = firstStartDate.plusDays(daysToAdd);
        final LocalDate secondStartDate = firstEndDate.plusDays(daysToAdd);
        final LocalDate secondEndDate = secondStartDate.plusDays(daysToAdd);

        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(firstStartDate, firstEndDate), 0, null);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(secondStartDate, secondEndDate), 0, null);

        //when
        //then
        assertDoesNotThrow(
                () -> new GoalRoomRoadmapNodes(List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode)));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 2, 3, 4, 5, 100})
    void 골룸_노드들_생성_시_기간이_겹칠_경우_예외를_던진다(final long value) {
        //given
        final LocalDate firstStartDate = LocalDate.now();
        final LocalDate firstEndDate = firstStartDate.plusDays(value);
        final LocalDate secondStartDate = firstEndDate.minusDays(value);
        final LocalDate secondEndDate = secondStartDate.plusDays(value);

        final GoalRoomRoadmapNode firstGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(firstStartDate, firstEndDate), 0, null);
        final GoalRoomRoadmapNode secondGoalRoomRoadmapNode = new GoalRoomRoadmapNode(
                new Period(secondStartDate, secondEndDate), 0, null);

        //when
        //then
        assertThatThrownBy(() -> new GoalRoomRoadmapNodes(List.of(firstGoalRoomRoadmapNode, secondGoalRoomRoadmapNode)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 골룸_노드_생성_시_빈_리스트가_들어오면_정상적으로_생성된다() {
        //given
        //when
        //then
        assertDoesNotThrow(() -> new GoalRoomRoadmapNodes(Collections.emptyList()));
    }

    @Test
    void 골룸_로드맵_노드들_중_첫번째_시작날짜를_구한다() {
        // given
        // when
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(new ArrayList<>(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 1, null),
                new GoalRoomRoadmapNode(new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER), 1, null))
        ));

        // expect
        assertThat(goalRoomRoadmapNodes.getGoalRoomStartDate()).isEqualTo(TODAY);
    }

    @Test
    void 골룸_로드맵_노드들_중_마지막_종료날짜를_구한다() {
        // given
        // when
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = new GoalRoomRoadmapNodes(new ArrayList<>(List.of(
                new GoalRoomRoadmapNode(new Period(TODAY, TEN_DAY_LATER), 1, null),
                new GoalRoomRoadmapNode(new Period(TWENTY_DAY_LAYER, THIRTY_DAY_LATER), 1, null))
        ));

        // then
        assertThat(goalRoomRoadmapNodes.getGoalRoomEndDate()).isEqualTo(THIRTY_DAY_LATER);
    }
}
