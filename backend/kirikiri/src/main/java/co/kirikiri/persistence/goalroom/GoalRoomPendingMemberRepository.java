package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRoomPendingMemberRepository extends JpaRepository<GoalRoomPendingMember, Long> {

    List<GoalRoomPendingMember> findByGoalRoom(final GoalRoom goalRoom);
}
