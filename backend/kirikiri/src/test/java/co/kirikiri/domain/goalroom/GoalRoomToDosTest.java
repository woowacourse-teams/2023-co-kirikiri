package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
import co.kirikiri.domain.goalroom.vo.Period;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomToDosTest {

    @Test
    void 아이디로_투두를_조회한다() {
        // given
        final GoalRoomToDo firstTodo = new GoalRoomToDo(1L, new GoalRoomTodoContent("투두1"),
                new Period(LocalDate.now(), LocalDate.now().plusDays(3)));
        final GoalRoomToDo secondTodo = new GoalRoomToDo(2L, new GoalRoomTodoContent("투두2"),
                new Period(LocalDate.now(), LocalDate.now().plusDays(5)));

        final GoalRoomToDos goalRoomToDos = new GoalRoomToDos(List.of(
                firstTodo, secondTodo
        ));

        // when
        final GoalRoomToDo findGoalRoomTodo = goalRoomToDos.findById(1L).get();

        // then
        assertThat(findGoalRoomTodo)
                .isEqualTo(firstTodo);
    }

    @Test
    void 아이디로_투두_조회시_없으면_빈값을_반환한다() {
        // given
        final GoalRoomToDo firstTodo = new GoalRoomToDo(1L, new GoalRoomTodoContent("투두1"),
                new Period(LocalDate.now(), LocalDate.now().plusDays(3)));
        final GoalRoomToDo secondTodo = new GoalRoomToDo(2L, new GoalRoomTodoContent("투두2"),
                new Period(LocalDate.now(), LocalDate.now().plusDays(5)));

        final GoalRoomToDos goalRoomToDos = new GoalRoomToDos(List.of(
                firstTodo, secondTodo
        ));

        // expected
        assertThat(goalRoomToDos.findById(3L))
                .isEmpty();
    }
}
