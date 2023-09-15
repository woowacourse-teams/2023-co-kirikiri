package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoalRoomRepository extends JpaRepository<GoalRoom, Long>, GoalRoomQueryRepository {

    @Override
    Optional<GoalRoom> findById(final Long goalRoomId);

    List<GoalRoom> findAllByEndDate(final LocalDate endDate);
}
