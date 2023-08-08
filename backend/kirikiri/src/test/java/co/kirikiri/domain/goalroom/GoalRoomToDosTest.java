package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.domain.goalroom.vo.GoalRoomTodoContent;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.exception.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
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
        final GoalRoomToDo findGoalRoomTodo = goalRoomToDos.findById(1L);

        // then
        Assertions.assertThat(findGoalRoomTodo)
                .isEqualTo(findGoalRoomTodo);
    }

    @Test
    void 아이디로_투두_조회시_없으면_예외가_발생한다() {
        // given
        final GoalRoomToDo firstTodo = new GoalRoomToDo(1L, new GoalRoomTodoContent("투두1"),
                new Period(LocalDate.now(), LocalDate.now().plusDays(3)));
        final GoalRoomToDo secondTodo = new GoalRoomToDo(2L, new GoalRoomTodoContent("투두2"),
                new Period(LocalDate.now(), LocalDate.now().plusDays(5)));

        final GoalRoomToDos goalRoomToDos = new GoalRoomToDos(List.of(
                firstTodo, secondTodo
        ));

        // expected
        assertThatThrownBy(() -> goalRoomToDos.findById(3L))
                .isInstanceOf(NotFoundException.class);
    }
}
