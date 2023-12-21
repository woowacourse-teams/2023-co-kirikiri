package co.kirikiri.checkfeed.service;

import co.kirikiri.checkfeed.domain.CheckFeed;
import co.kirikiri.checkfeed.persistence.CheckFeedRepository;
import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.service.FileService;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.service.DashBoardCheckFeedService;
import co.kirikiri.goalroom.service.dto.response.DashBoardCheckFeedResponse;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class DashBoardCheckFeedServiceImpl implements DashBoardCheckFeedService {

    private final CheckFeedRepository checkFeedRepository;
    private final FileService fileService;

    @Override
    @Transactional(readOnly = true)
    public List<DashBoardCheckFeedResponse> findCheckFeedsByNodeAndGoalRoomStatus(final GoalRoom goalRoom,
                                                                                  final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode) {
        final List<CheckFeed> checkFeeds = findCheckFeeds(goalRoom, currentGoalRoomRoadmapNode);
        return makeCheckFeedResponses(checkFeeds);
    }

    private List<CheckFeed> findCheckFeeds(final GoalRoom goalRoom,
                                           final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode) {
        if (goalRoom.isCompleted()) {
            return checkFeedRepository.findByGoalRoomIdOrderByCreatedAtDesc(goalRoom.getId());
        }
        if (goalRoom.isRunning() && currentGoalRoomRoadmapNode.isPresent()) {
            return checkFeedRepository.findByGoalRoomRoadmapNodeIdOrderByCreatedAtDesc(
                    currentGoalRoomRoadmapNode.get().getId());
        }
        return Collections.emptyList();
    }

    private List<DashBoardCheckFeedResponse> makeCheckFeedResponses(final List<CheckFeed> checkFeeds) {
        return checkFeeds.stream()
                .map(this::makeCheckFeedResponse)
                .toList();
    }

    private DashBoardCheckFeedResponse makeCheckFeedResponse(final CheckFeed checkFeed) {
        final URL checkFeedImageUrl = fileService.generateUrl(checkFeed.getServerFilePath(), HttpMethod.GET);
        return new DashBoardCheckFeedResponse(checkFeed.getId(), checkFeedImageUrl.toExternalForm(),
                checkFeed.getDescription(), checkFeed.getCreatedAt().toLocalDate());
    }
}
