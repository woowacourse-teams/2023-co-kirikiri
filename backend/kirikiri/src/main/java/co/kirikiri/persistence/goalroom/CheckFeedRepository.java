package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheckFeedRepository extends JpaRepository<CheckFeed, Long> {

    @Query("SELECT cf"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMember = :goalRoomMember"
            + " AND cf.createdAt >= :start"
            + " AND cf.createdAt < :end")
    Optional<CheckFeed> findByGoalRoomMemberAndDateTime(final GoalRoomMember goalRoomMember, final LocalDateTime start,
                                                        final LocalDateTime end);

    @Query("SELECT Count(cf)"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMember = :goalRoomMember")
    int countByGoalRoomMember(final GoalRoomMember goalRoomMember);

    @Query("SELECT Count(cf)"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMember = :goalRoomMember"
            + " AND cf.goalRoomRoadmapNode = :goalRoomRoadmapNode")
    int countByGoalRoomMemberAndGoalRoomRoadmapNode(final GoalRoomMember goalRoomMember,
                                                    final GoalRoomRoadmapNode goalRoomRoadmapNode);

    List<CheckFeed> findByGoalRoomRoadmapNode(final GoalRoomRoadmapNode goalRoomRoadmapNode);
}
