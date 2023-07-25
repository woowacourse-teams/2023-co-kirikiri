package co.kirikiri.controller;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.GoalRoomService;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goal-rooms")
@RequiredArgsConstructor
public class GoalRoomController {

    private final GoalRoomService goalRoomService;

    @GetMapping("/{goalRoomId}")
    public ResponseEntity<GoalRoomResponse> findGoalRoom(@PathVariable("goalRoomId") final Long goalRoomId) {
        final GoalRoomResponse goalRoomResponse = goalRoomService.findGoalRoom(goalRoomId);
        return ResponseEntity.ok(goalRoomResponse);
    }

    @Authenticated
    @GetMapping(value = "/{goalRoomId}", headers = "Authorization")
    public ResponseEntity<GoalRoomCertifiedResponse> findGoalRoom(@MemberIdentifier final String identifier,
                                                                  @PathVariable("goalRoomId") final Long goalRoomId) {
        final GoalRoomCertifiedResponse goalRoomResponse = goalRoomService.findGoalRoom(identifier, goalRoomId);
        return ResponseEntity.ok(goalRoomResponse);
    }
}
