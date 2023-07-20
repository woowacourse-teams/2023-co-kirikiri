package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRoomPendingMemberRepository extends JpaRepository<GoalRoomPendingMember, Long> {

}
