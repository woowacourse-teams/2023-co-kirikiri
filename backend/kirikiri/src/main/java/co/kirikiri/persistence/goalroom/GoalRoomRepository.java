package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRoomRepository extends JpaRepository<GoalRoom, Long>, GoalRoomQueryRepository {
}
