package co.kirikiri.goalroom.persistence;

import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRoomMemberRepository extends JpaRepository<GoalRoomMember, Long>,
        GoalRoomMemberQueryRepository, GoalRoomMemberJdbcRepository {

    @Query("select gm from GoalRoomMember gm "
            + "inner join fetch gm.goalRoom g "
            + "where g=:goalRoom "
            + "and gm.memberId =:memberId")
    Optional<GoalRoomMember> findByGoalRoomAndMemberId(
            @Param("goalRoom") final GoalRoom goalRoom,
            @Param("memberId") final Long memberId);

    @Query("select gm from GoalRoomMember gm "
            + "join fetch gm.goalRoom g "
            + "where g=:goalRoom ")
    List<GoalRoomMember> findAllByGoalRoom(@Param("goalRoom") final GoalRoom goalRoom);
}
