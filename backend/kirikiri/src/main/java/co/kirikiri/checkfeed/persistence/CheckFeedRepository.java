package co.kirikiri.checkfeed.persistence;

import co.kirikiri.checkfeed.domain.CheckFeed;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckFeedRepository extends JpaRepository<CheckFeed, Long> {

    @Query("SELECT cf"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMemberId = :goalRoomMemberId"
            + " AND cf.createdAt >= :start"
            + " AND cf.createdAt < :end")
    Optional<CheckFeed> findByGoalRoomMemberIdAndDateTime(final Long goalRoomMemberId, final LocalDateTime start,
                                                          final LocalDateTime end);

    @Query("SELECT COUNT(cf)"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMemberId = :goalRoomMemberId"
            + " AND cf.goalRoomRoadmapNodeId = :goalRoomRoadmapNodeId")
    int countByGoalRoomMemberIdAndGoalRoomRoadmapNodeId(final Long goalRoomMemberId, final Long goalRoomRoadmapNodeId);

    @Query(value = "SELECT cf.* FROM check_feed as cf "
            + "LEFT JOIN goal_room_member as gm ON cf.goal_room_member_id = gm.id "
            + "JOIN goal_room as g ON gm.goal_room_id = g.id "
            + "WHERE g.id = :goalRoomId "
            + "ORDER BY cf.created_at DESC ", nativeQuery = true)
    List<CheckFeed> findByGoalRoomIdOrderByCreatedAtDesc(@Param("goalRoomId") final Long goalRoomId);

    List<CheckFeed> findByGoalRoomRoadmapNodeIdOrderByCreatedAtDesc(final Long goalRoomRoadmapNodeId);
}
