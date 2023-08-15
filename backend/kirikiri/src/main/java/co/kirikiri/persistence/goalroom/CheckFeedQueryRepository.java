package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import java.util.List;
import java.util.Optional;

public interface CheckFeedQueryRepository {

    List<CheckFeed> findByGoalRoomRoadmapNodeAndGoalRoomStatusWithMemberAndMemberImage(
            final Optional<GoalRoomRoadmapNode> goalRoomRoadmapNode, final GoalRoomStatus status);

    List<CheckFeed> findByGoalRoomRoadmapNodeAndGoalRoomStatus(
            final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode, final GoalRoomStatus status);
}
