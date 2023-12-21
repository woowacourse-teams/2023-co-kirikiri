package co.kirikiri.goalroom.persistence;

import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRoomPendingMemberRepository extends JpaRepository<GoalRoomPendingMember, Long>,
        GoalRoomPendingMemberQueryRepository {

    @Query("select gp from GoalRoomPendingMember gp "
            + "inner join fetch gp.goalRoom g "
            + "where g=:goalRoom "
            + "and gp.memberId =:memberId")
    Optional<GoalRoomPendingMember> findByGoalRoomAndMemberId(
            @Param("goalRoom") final GoalRoom goalRoom, @Param("memberId") final Long memberId);

    List<GoalRoomPendingMember> findByGoalRoom(final GoalRoom goalRoom);

    @Query("select gp from GoalRoomPendingMember gp "
            + "join fetch gp.goalRoom g "
            + "where g=:goalRoom ")
    List<GoalRoomPendingMember> findAllByGoalRoom(@Param("goalRoom") final GoalRoom goalRoom);

    @Modifying
    @Query("DELETE FROM GoalRoomPendingMember gp WHERE gp.id IN :ids")
    void deleteAllByIdIn(@Param("ids") final List<Long> ids);
}
