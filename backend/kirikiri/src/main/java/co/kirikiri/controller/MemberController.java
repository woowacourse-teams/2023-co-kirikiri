package co.kirikiri.controller;

import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import co.kirikiri.service.GoalRoomReadService;
import co.kirikiri.service.MemberService;
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.member.request.MemberJoinRequest;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final GoalRoomReadService goalRoomReadService;

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody @Valid final MemberJoinRequest request) {
        final Long memberId = memberService.join(request);
        return ResponseEntity.created(URI.create("/api/members/" + memberId)).build();
    }

    @Authenticated
    @GetMapping("/goal-rooms/{goalRoomId}")
    public ResponseEntity<MemberGoalRoomResponse> findMemberGoalRoom(
            @MemberIdentifier final String identifier, @PathVariable final Long goalRoomId) {
        final MemberGoalRoomResponse memberGoalRoomResponse = goalRoomReadService.findMemberGoalRoom(identifier,
                goalRoomId);
        return ResponseEntity.ok(memberGoalRoomResponse);
    }

    @Authenticated
    @GetMapping("/goal-rooms")
    public ResponseEntity<List<MemberGoalRoomForListResponse>> findMemberGoalRoomsByStatus(
            @MemberIdentifier final String identifier,
            @RequestParam(value = "statusCond", required = false) final GoalRoomStatusTypeRequest goalRoomStatusTypeRequest) {
        if (goalRoomStatusTypeRequest == null) {
            final List<MemberGoalRoomForListResponse> memberGoalRoomForListResponses =
                    goalRoomReadService.findMemberGoalRooms(identifier);
            return ResponseEntity.ok(memberGoalRoomForListResponses);
        }
        final List<MemberGoalRoomForListResponse> memberGoalRoomForListResponses =
                goalRoomReadService.findMemberGoalRoomsByStatusType(identifier, goalRoomStatusTypeRequest);
        return ResponseEntity.ok(memberGoalRoomForListResponses);
    }
}
