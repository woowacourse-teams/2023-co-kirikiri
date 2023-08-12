package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import java.util.List;

public interface CheckFeedQueryRepository {

    List<CheckFeed> findByGoalRoomRoadmapNodeAndGoalRoomStatusWithMemberAndMemberImage(
            final GoalRoomRoadmapNode goalRoomRoadmapNode, final GoalRoomStatus status);

    List<CheckFeed> findByGoalRoomRoadmapNodeAndGoalRoomStatus(
            final GoalRoomRoadmapNode currentGoalRoomRoadmapNode, final GoalRoomStatus status);
}
