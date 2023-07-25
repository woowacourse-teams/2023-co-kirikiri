package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.exception.BadRequestException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GoalRoomRoadmapNodeTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void 정상적으로_골룸_로드맵_노드를_생성한다(final int daysToAdd) {
        //given
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.plusDays(daysToAdd);

        //when
        //then
        assertDoesNotThrow(() -> new GoalRoomRoadmapNode(new Period(startDate, endDate), daysToAdd, new RoadmapNode("title", "content")));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3, -4, -5, -6, -7})
    void 골름_노드의_인증_횟수가_음수일때_예외를_던진다(final int checkCount) {
        //given
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.plusDays(0);

        //when
        //then
        assertThatThrownBy(() -> new GoalRoomRoadmapNode(new Period(startDate, endDate), checkCount, new RoadmapNode("title", "content")))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void 골룸_로드맵_노드를_생성할때_기간보다_인증_횟수가_크면_예외를_던진다(final int checkCount) {
        //given
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = LocalDate.now();

        //when
        //then
        assertThatThrownBy(() -> new GoalRoomRoadmapNode(new Period(startDate, endDate), checkCount, new RoadmapNode("title", "content")))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void 골룸_로드맵_노드를_생성할때_시작날짜가_오늘보다_전일_경우_예외를_던진다(final long daysToSubtract) {
        //given
        final LocalDate startDate = LocalDate.now().minusDays(daysToSubtract);
        final LocalDate endDate = startDate.plusDays(7);
        final int checkCount = 7;

        //when
        //then
        assertThatThrownBy(() -> new GoalRoomRoadmapNode(new Period(startDate, endDate), checkCount, new RoadmapNode("title", "content")))
                .isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void 골룸_로드맵_노드를_생성할때_시작날짜가_종료날짜보다_후일_경우_예외를_던진다(final long daysToSubtract) {
        //given
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.minusDays(daysToSubtract);
        final int checkCount = 0;

        //when
        //then
        assertThatThrownBy(() -> new GoalRoomRoadmapNode(new Period(startDate, endDate), checkCount, new RoadmapNode("title", "content")))
                .isInstanceOf(BadRequestException.class);
    }
}
