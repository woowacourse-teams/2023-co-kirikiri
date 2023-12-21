package co.kirikiri.goalroom.persistence;

import co.kirikiri.goalroom.domain.GoalRoom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRoomRepository extends JpaRepository<GoalRoom, Long>, GoalRoomQueryRepository {

    @Override
    Optional<GoalRoom> findById(final Long goalRoomId);

    List<GoalRoom> findAllByEndDate(final LocalDate endDate);

    List<GoalRoom> findByRoadmapContentId(final Long roadmapContentId);
}
