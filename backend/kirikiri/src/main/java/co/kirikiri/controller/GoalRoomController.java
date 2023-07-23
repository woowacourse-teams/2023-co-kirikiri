package co.kirikiri.controller;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.service.GoalRoomService;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goal-rooms")
@RequiredArgsConstructor
public class GoalRoomController {

    private final GoalRoomService goalRoomService;

    @Authenticated
    @GetMapping("/{goalRoomId}")
    public ResponseEntity<GoalRoomResponse> findMemberGoalRoom(
            @RequestParam(value = "member", required = true) final Long memberId,
            @PathVariable final Long goalRoomId) {
        final GoalRoomResponse goalRoomResponse = goalRoomService.findMemberGoalRoom(memberId, goalRoomId);
        return ResponseEntity.ok(goalRoomResponse);
    }

    @Authenticated
    @GetMapping
    public ResponseEntity<PageResponse<GoalRoomForListResponse>> findMemberGoalRooms(
            @RequestParam(value = "member", required = true) final Long memberId,
            @ModelAttribute final CustomPageRequest pageRequest) {
        final PageResponse<GoalRoomForListResponse> goalRoomsPageResponse = goalRoomService.findMemberGoalRooms(
                memberId, pageRequest);
        return ResponseEntity.ok(goalRoomsPageResponse);
    }
}
