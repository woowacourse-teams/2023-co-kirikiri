package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRoomRepository extends JpaRepository<GoalRoom, Long>, GoalRoomQueryRepository {

    @Override
    Optional<GoalRoom> findById(final Long goalRoomId);
}
