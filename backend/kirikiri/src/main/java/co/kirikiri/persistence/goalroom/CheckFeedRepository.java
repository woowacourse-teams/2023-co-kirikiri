package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheckFeedRepository extends JpaRepository<CheckFeed, Long> {

    @Query("SELECT COUNT(cf) > 0"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMember = :goalRoomMember"
            + " AND cf.goalRoomRoadmapNode = :goalRoomRoadmapNode"
            + " AND cf.createdAt >= :start"
            + " AND cf.createdAt <= :end")
    boolean isMemberUploadCheckFeedToday(final GoalRoomMember goalRoomMember,
                                         final GoalRoomRoadmapNode goalRoomRoadmapNode,
                                         final LocalDateTime start, final LocalDateTime end);

    @Query("SELECT COUNT(cf)"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMember = :goalRoomMember"
            + " AND cf.goalRoomRoadmapNode = :goalRoomRoadmapNode")
    int findCountByGoalRoomMemberAndGoalRoomRoadmapNode(final GoalRoomMember goalRoomMember, final
    GoalRoomRoadmapNode goalRoomRoadmapNode);
}
