package co.kirikiri.goalroom.persistence;

import co.kirikiri.goalroom.domain.GoalRoom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoalRoomRepository extends JpaRepository<GoalRoom, Long>, GoalRoomQueryRepository {

    @Override
    Optional<GoalRoom> findById(final Long goalRoomId);

    List<GoalRoom> findAllByEndDate(final LocalDate endDate);

    List<GoalRoom> findByRoadmapContentId(final Long roadmapContentId);

    @Query(value = "SELECT g.* FROM goal_room as g "
            + "LEFT JOIN goal_room_pending_member as gpm ON gpm.goal_room_id = g.id "
            + "LEFT JOIN goal_room_member as gm ON gm.goal_room_id = g.id "
            + "WHERE gpm.member_id = :memberId OR gm.member_id = :memberId ", nativeQuery = true)
    List<GoalRoom> findByMemberId(final Long memberId);

    @Query(value = "SELECT g.* FROM goal_room as g "
            + "LEFT JOIN goal_room_pending_member as gpm ON gpm.goal_room_id = g.id "
            + "LEFT JOIN goal_room_member as gm ON gm.goal_room_id = g.id "
            + "WHERE (gpm.member_id = :memberId OR gm.member_id = :memberId) "
            + "AND g.status = :goalRoomStatus ", nativeQuery = true)
    List<GoalRoom> findByMemberAndStatus(final Long memberId, final String goalRoomStatus);

}
