package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CheckFeedRepository extends JpaRepository<CheckFeed, Long>, CheckFeedQueryRepository {

    @Query(value = "SELECT *"
            + " FROM check_feed"
            + " WHERE goal_room_member_id = :goalRoomMemberId"
            + " AND created_at >= :start"
            + " AND created_at < :end",
            nativeQuery = true)
    Optional<CheckFeed> findByGoalRoomMemberAndDateTime(final Long goalRoomMemberId, final LocalDateTime start,
                                                        final LocalDateTime end);

    @Query("SELECT COUNT(cf)"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMember = :goalRoomMember")
    int countByGoalRoomMember(final GoalRoomMember goalRoomMember);

    @Query(value = "SELECT COUNT(*)"
            + " FROM check_feed"
            + " WHERE goal_room_member_id = :goalRoomMemberId"
            + " AND goal_room_roadmap_node_id = :goalRoomRoadmapNodeId",
            nativeQuery = true)
    int countByGoalRoomMemberAndGoalRoomRoadmapNode(final Long goalRoomMemberId,
                                                    final Long goalRoomRoadmapNodeId);

    @Query("SELECT cf"
            + " FROM CheckFeed cf"
            + " WHERE cf.goalRoomMember.goalRoom =:goalRoom"
            + " ORDER BY cf.createdAt DESC")
    List<CheckFeed> findByGoalRoom(final GoalRoom goalRoom);

    List<CheckFeed> findByGoalRoomRoadmapNode(final GoalRoomRoadmapNode goalRoomRoadmapNode);
}
