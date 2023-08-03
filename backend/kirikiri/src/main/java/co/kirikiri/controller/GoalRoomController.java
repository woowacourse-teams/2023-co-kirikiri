package co.kirikiri.controller;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.GoalRoomCreateService;
import co.kirikiri.service.GoalRoomReadService;
import co.kirikiri.service.dto.goalroom.request.CheckFeedRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.request.GoalRoomTodoRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goal-rooms")
@RequiredArgsConstructor
public class GoalRoomController {

    private final GoalRoomCreateService goalRoomCreateService;
    private final GoalRoomReadService goalRoomReadService;

    @PostMapping
    @Authenticated
    public ResponseEntity<Void> create(@RequestBody @Valid final GoalRoomCreateRequest request,
                                       @MemberIdentifier final String identifier) {
        final Long id = goalRoomCreateService.create(request, identifier);
        return ResponseEntity.created(URI.create("/api/goal-rooms/" + id)).build();
    }

    @Authenticated
    @GetMapping(value = "/{goalRoomId}", headers = "Authorization")
    public ResponseEntity<GoalRoomCertifiedResponse> findGoalRoom(@MemberIdentifier final String identifier,
                                                                  @PathVariable("goalRoomId") final Long goalRoomId) {
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomReadService.findGoalRoom(identifier, goalRoomId);
        return ResponseEntity.ok(goalRoomResponse);
    }

    @Authenticated
    @PostMapping("/{goalRoomId}/join")
    public ResponseEntity<Void> joinGoalRoom(@MemberIdentifier final String identifier,
                                             @PathVariable final Long goalRoomId) {
        goalRoomCreateService.join(identifier, goalRoomId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Authenticated
    @PostMapping("/{goalRoomId}/todos")
    public ResponseEntity<Void> addTodo(@RequestBody @Valid final GoalRoomTodoRequest goalRoomTodoRequest,
                                        @PathVariable final Long goalRoomId,
                                        @MemberIdentifier final String identifier) {
        final Long id = goalRoomCreateService.addGoalRoomTodo(goalRoomId, identifier, goalRoomTodoRequest);
        return ResponseEntity.created(URI.create("/api/goal-rooms/" + goalRoomId + "/todos/" + id)).build();
    }

    @Authenticated
    @PostMapping(value = "/{goalRoomId}/checkFeeds", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> createCheckFeed(@MemberIdentifier final String identifier,
                                                @PathVariable("goalRoomId") final Long goalRoomId,
                                                @ModelAttribute final CheckFeedRequest checkFeedRequest) {
        final String imageUrl = goalRoomCreateService.createCheckFeed(identifier, goalRoomId, checkFeedRequest);
        return ResponseEntity.created(URI.create(imageUrl)).build();
    }

    @GetMapping("/{goalRoomId}")
    public ResponseEntity<GoalRoomResponse> findGoalRoom(@PathVariable("goalRoomId") final Long goalRoomId) {
        final GoalRoomResponse goalRoomResponse = goalRoomReadService.findGoalRoom(goalRoomId);
        return ResponseEntity.ok(goalRoomResponse);
    }

    @Authenticated
    @GetMapping("/{goalRoomId}/members")
    public ResponseEntity<List<GoalRoomMemberResponse>> findGoalRoomMembers(@PathVariable final Long goalRoomId) {
        final List<GoalRoomMemberResponse> goalRoomMembers = goalRoomReadService.findGoalRoomMembers(goalRoomId);
        return ResponseEntity.ok(goalRoomMembers);
    }
}
