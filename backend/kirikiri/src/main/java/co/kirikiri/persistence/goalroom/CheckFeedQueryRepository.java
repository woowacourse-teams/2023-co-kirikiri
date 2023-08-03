package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import java.util.List;

public interface CheckFeedQueryRepository {

    List<CheckFeed> findByGoalRoomRoadmapNodeWithGoalRoomMemberAndMemberImage(
            final GoalRoomRoadmapNode goalRoomRoadmapNode);
}
