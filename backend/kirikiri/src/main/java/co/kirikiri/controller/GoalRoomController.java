package co.kirikiri.controller;

import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.GoalRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goal-rooms")
@RequiredArgsConstructor
public class GoalRoomController {

    private final GoalRoomService goalRoomService;

    @PostMapping("/{goalRoomId}/join")
    public ResponseEntity<Void> joinGoalRoom(@MemberIdentifier final String identifier,
                                             @PathVariable final Long goalRoomId) {
        goalRoomService.join(identifier, goalRoomId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
