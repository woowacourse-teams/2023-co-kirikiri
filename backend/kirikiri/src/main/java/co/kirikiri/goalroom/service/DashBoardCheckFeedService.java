package co.kirikiri.goalroom.service;

import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.service.dto.response.DashBoardCheckFeedResponse;
import java.util.List;
import java.util.Optional;

public interface DashBoardCheckFeedService {

    List<DashBoardCheckFeedResponse> findCheckFeedsByNodeAndGoalRoomStatus(final GoalRoom goalRoom,
                                                                           final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode);
}
