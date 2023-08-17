package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import java.util.List;

public interface CheckFeedQueryRepository {

    List<CheckFeed> findByRunningGoalRoomRoadmapNodeWithMemberAndMemberImage(final GoalRoomRoadmapNode goalRoomRoadmapNode);

    List<CheckFeed> findByRunningGoalRoomRoadmapNode(final GoalRoomRoadmapNode currentGoalRoomRoadmapNode);

    List<CheckFeed> findByGoalRoomWithMemberAndMemberImage(final GoalRoom goalRoom);
}
