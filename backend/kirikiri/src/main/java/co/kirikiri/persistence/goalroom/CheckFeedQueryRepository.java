package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import java.util.List;

public interface CheckFeedQueryRepository {

    List<CheckFeed> findByGoalRoomRoadmapNodeWithGoalRoomMemberAndMemberImage(
            final GoalRoomRoadmapNode goalRoomRoadmapNode);

    List<CheckFeed> findByGoalRoomRoadmapNodeAndGoalRoomStatus(
            final GoalRoomRoadmapNode currentGoalRoomRoadmapNode, final GoalRoomStatus status);
}
