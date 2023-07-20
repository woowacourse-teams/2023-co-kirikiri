package co.kirikiri.controller;

import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.GoalRoomService;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/goal-rooms")
@RequiredArgsConstructor
public class GoalRoomController {

    private final GoalRoomService goalRoomService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid final GoalRoomCreateRequest request, @MemberIdentifier final String identifier) {
        final Long id = goalRoomService.create(request, identifier);
        return ResponseEntity.created(URI.create("/api/goal-rooms/" + id)).build();
    }
}
