package co.kirikiri.checkfeed.controller;

import co.kirikiri.checkfeed.service.GoalRoomCheckFeedService;
import co.kirikiri.checkfeed.service.dto.request.CheckFeedRequest;
import co.kirikiri.checkfeed.service.dto.response.GoalRoomCheckFeedResponse;
import co.kirikiri.common.interceptor.Authenticated;
import co.kirikiri.common.resolver.MemberIdentifier;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goal-rooms/{goalRoomId}/checkFeeds")
@RequiredArgsConstructor
public class GoalRoomCheckFeedController {

    private final GoalRoomCheckFeedService goalRoomCheckFeedService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Authenticated
    public ResponseEntity<Void> createCheckFeed(@MemberIdentifier final String identifier,
                                                @PathVariable("goalRoomId") final Long goalRoomId,
                                                @ModelAttribute final CheckFeedRequest checkFeedRequest) {
        final String imageUrl = goalRoomCheckFeedService.createCheckFeed(identifier, goalRoomId, checkFeedRequest);
        return ResponseEntity.created(URI.create(imageUrl)).build();
    }

    @GetMapping
    @Authenticated
    public ResponseEntity<List<GoalRoomCheckFeedResponse>> findGoalRoomCheckFeeds(
            @MemberIdentifier final String identifier,
            @PathVariable("goalRoomId") final Long goalRoomId) {
        final List<GoalRoomCheckFeedResponse> response = goalRoomCheckFeedService.findGoalRoomCheckFeeds(identifier,
                goalRoomId);
        return ResponseEntity.ok(response);
    }
}
