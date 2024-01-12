package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.goalroom.exception.GoalRoomException;
import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
import co.kirikiri.domain.goalroom.vo.Period;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GoalRoomToDoTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    void 정상적으로_골룸_투두를_생성한다(final int daysToAdd) {
        //given
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.plusDays(daysToAdd);

        //when
        //then
        assertDoesNotThrow(() -> new GoalRoomToDo(new GoalRoomTodoContent("content"), new Period(startDate, endDate)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void 골룸_투두를_생성할때_시작날짜가_오늘보다_전일_경우_예외를_던진다(final long daysToSubtract) {
        //given
        final LocalDate startDate = LocalDate.now().minusDays(daysToSubtract);
        final LocalDate endDate = startDate.plusDays(7);

        //when
        //then
        assertThatThrownBy(() -> new GoalRoomToDo(new GoalRoomTodoContent("content"), new Period(startDate, endDate)))
                .isInstanceOf(GoalRoomException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5, 6, 7})
    void 골룸_투두를_생성할때_시작날짜가_종료날짜보다_후일_경우_예외를_던진다(final long daysToSubtract) {
        //given
        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = startDate.minusDays(daysToSubtract);

        //when
        //then
        assertThatThrownBy(() -> new GoalRoomToDo(new GoalRoomTodoContent("content"), new Period(startDate, endDate)))
                .isInstanceOf(GoalRoomException.class);
    }
}
