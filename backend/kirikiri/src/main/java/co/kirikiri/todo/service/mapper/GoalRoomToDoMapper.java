package co.kirikiri.todo.service.mapper;

import co.kirikiri.goalroom.service.dto.response.DashBoardToDoCheckResponse;
import co.kirikiri.goalroom.service.dto.response.DashBoardToDoResponse;
import co.kirikiri.todo.domain.GoalRoomToDo;
import co.kirikiri.todo.domain.GoalRoomToDoCheck;
import co.kirikiri.todo.domain.GoalRoomToDos;
import co.kirikiri.todo.domain.vo.GoalRoomTodoContent;
import co.kirikiri.todo.domain.vo.ToDoPeriod;
import co.kirikiri.todo.service.dto.request.GoalRoomTodoRequest;
import co.kirikiri.todo.service.dto.response.GoalRoomToDoCheckResponse;
import co.kirikiri.todo.service.dto.response.GoalRoomTodoResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoalRoomToDoMapper {

    private static final int MAX_MEMBER_GOAL_ROOM_TODO_NUMBER = 3;

    public static GoalRoomToDo convertToGoalRoomTodo(final GoalRoomTodoRequest goalRoomTodoRequest, final Long goalRoomId) {
        return new GoalRoomToDo(goalRoomId, new GoalRoomTodoContent(goalRoomTodoRequest.content()),
                new ToDoPeriod(goalRoomTodoRequest.startDate(), goalRoomTodoRequest.endDate()));
    }

    public static List<GoalRoomTodoResponse> convertGoalRoomTodoResponses(final GoalRoomToDos goalRoomToDos,
                                                                          final List<GoalRoomToDoCheck> checkedTodos) {
        return goalRoomToDos.getValues().stream()
                .map(goalRoomToDo -> convertGoalRoomTodoResponse(checkedTodos, goalRoomToDo))
                .toList();
    }

    private static GoalRoomTodoResponse convertGoalRoomTodoResponse(final List<GoalRoomToDoCheck> checkedTodos,
                                                                    final GoalRoomToDo goalRoomToDo) {
        final GoalRoomToDoCheckResponse checkResponse = new GoalRoomToDoCheckResponse(
                isCheckedTodo(goalRoomToDo.getId(), checkedTodos));
        return new GoalRoomTodoResponse(goalRoomToDo.getId(), goalRoomToDo.getContent(), goalRoomToDo.getStartDate(),
                goalRoomToDo.getEndDate(), checkResponse);
    }

    private static boolean isCheckedTodo(final Long targetTodoId, final List<GoalRoomToDoCheck> checkedTodos) {
        final List<Long> checkTodoIds = checkedTodos.stream()
                .map(goalRoomToDoCheck -> goalRoomToDoCheck.getGoalRoomToDo().getId())
                .toList();
        return checkTodoIds.contains(targetTodoId);
    }

    public static List<DashBoardToDoResponse> convertToDashBoardTodoResponsesLimit(final GoalRoomToDos goalRoomToDos,
                                                                                   final List<GoalRoomToDoCheck> checkedTodos) {
        return goalRoomToDos.getValues()
                .stream()
                .map(goalRoomToDo -> convertToDashBoardTodoResponse(checkedTodos, goalRoomToDo))
                .limit(MAX_MEMBER_GOAL_ROOM_TODO_NUMBER)
                .toList();
    }

    private static DashBoardToDoResponse convertToDashBoardTodoResponse(final List<GoalRoomToDoCheck> checkedTodos,
                                                                        final GoalRoomToDo goalRoomToDo) {
        final DashBoardToDoCheckResponse checkResponse = new DashBoardToDoCheckResponse(
                isCheckedTodo(goalRoomToDo.getId(), checkedTodos));
        return new DashBoardToDoResponse(goalRoomToDo.getId(),
                goalRoomToDo.getContent(),
                goalRoomToDo.getStartDate(), goalRoomToDo.getEndDate(),
                checkResponse);
    }
}
