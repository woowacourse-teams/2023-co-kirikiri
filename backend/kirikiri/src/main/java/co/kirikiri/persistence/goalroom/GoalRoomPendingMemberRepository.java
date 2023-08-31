package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.member.vo.Identifier;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRoomPendingMemberRepository extends JpaRepository<GoalRoomPendingMember, Long>,
        GoalRoomPendingMemberQueryRepository {

    @Query("select gp from GoalRoomPendingMember gp "
            + "inner join fetch gp.goalRoom g "
            + "inner join fetch gp.member m "
            + "where g=:goalRoom "
            + "and m.identifier =:identifier")
    Optional<GoalRoomPendingMember> findByGoalRoomAndMemberIdentifier(
            @Param("goalRoom") final GoalRoom goalRoom, @Param("identifier") final Identifier identifier);

    List<GoalRoomPendingMember> findByGoalRoom(final GoalRoom goalRoom);

    @Query("select gp from GoalRoomPendingMember gp "
            + "join fetch gp.goalRoom g "
            + "join fetch gp.member m "
            + "where g=:goalRoom "
            + "and gp.member = m")
    List<GoalRoomPendingMember> findAllByGoalRoom(@Param("goalRoom") final GoalRoom goalRoom);
}
