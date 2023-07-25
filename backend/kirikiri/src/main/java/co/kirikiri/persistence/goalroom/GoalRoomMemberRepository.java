package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.member.vo.Identifier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRoomMemberRepository extends JpaRepository<GoalRoomMember, Long> {

    @Query("select gm from GoalRoomMember gm "
            + "inner join gm.goalRoom g "
            + "inner join gm.member m "
            + "where g=:goalRoom "
            + "and m.identifier =:identifier")
    Optional<GoalRoomMember> findByGoalRoomAndMemberIdentifier(
            @Param("goalRoom") final GoalRoom goalRoom, @Param("identifier") final Identifier identifier);
}
