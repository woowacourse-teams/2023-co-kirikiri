package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRoomMemberRepository extends JpaRepository<GoalRoomMember, Long>, GoalRoomMemberQueryRepository {

}
