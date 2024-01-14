package co.kirikiri.todo.controller;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.todo.service.GoalRoomToDoService;
import co.kirikiri.todo.service.dto.request.GoalRoomTodoRequest;
import co.kirikiri.todo.service.dto.response.GoalRoomToDoCheckResponse;
import co.kirikiri.todo.service.dto.response.GoalRoomTodoResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goal-rooms/{goalRoomId}/todos")
@RequiredArgsConstructor
public class GoalRoomToDoController {

    private final GoalRoomToDoService goalRoomToDoService;

    @PostMapping
    @Authenticated
    public ResponseEntity<Void> addTodo(@RequestBody @Valid final GoalRoomTodoRequest goalRoomTodoRequest,
                                        @PathVariable final Long goalRoomId,
                                        @MemberIdentifier final String identifier) {
        final Long id = goalRoomToDoService.addGoalRoomTodo(goalRoomId, identifier, goalRoomTodoRequest);
        return ResponseEntity.created(URI.create("/api/goal-rooms/" + goalRoomId + "/todos/" + id)).build();
    }

    @PostMapping("/{todoId}")
    @Authenticated
    public ResponseEntity<GoalRoomToDoCheckResponse> checkTodo(@PathVariable final Long goalRoomId,
                                                               @PathVariable final Long todoId,
                                                               @MemberIdentifier final String identifier) {
        final GoalRoomToDoCheckResponse checkResponse = goalRoomToDoService.checkGoalRoomTodo(goalRoomId, todoId,
                identifier);
        return ResponseEntity.ok(checkResponse);
    }

    @GetMapping
    @Authenticated
    public ResponseEntity<List<GoalRoomTodoResponse>> findAllTodos(
            @PathVariable final Long goalRoomId,
            @MemberIdentifier final String identifier) {
        final List<GoalRoomTodoResponse> todoResponses = goalRoomToDoService.findAllGoalRoomTodo(goalRoomId,
                identifier);
        return ResponseEntity.ok(todoResponses);
    }
}
