package co.kirikiri.todo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.todo.domain.vo.GoalRoomTodoContent;
import co.kirikiri.todo.domain.vo.ToDoPeriod;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomToDosTest {

    @Test
    void 아이디로_투두를_조회한다() {
        // given
        final GoalRoomToDo firstTodo = new GoalRoomToDo(1L, null, new GoalRoomTodoContent("투두1"),
                new ToDoPeriod(LocalDate.now(), LocalDate.now().plusDays(3)));
        final GoalRoomToDo secondTodo = new GoalRoomToDo(2L, null, new GoalRoomTodoContent("투두2"),
                new ToDoPeriod(LocalDate.now(), LocalDate.now().plusDays(5)));

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
        final GoalRoomToDo firstTodo = new GoalRoomToDo(1L, null, new GoalRoomTodoContent("투두1"),
                new ToDoPeriod(LocalDate.now(), LocalDate.now().plusDays(3)));
        final GoalRoomToDo secondTodo = new GoalRoomToDo(2L, null, new GoalRoomTodoContent("투두2"),
                new ToDoPeriod(LocalDate.now(), LocalDate.now().plusDays(5)));

        final GoalRoomToDos goalRoomToDos = new GoalRoomToDos(List.of(
                firstTodo, secondTodo
        ));

        // expected
        assertThat(goalRoomToDos.findById(3L))
                .isEmpty();
    }
}
