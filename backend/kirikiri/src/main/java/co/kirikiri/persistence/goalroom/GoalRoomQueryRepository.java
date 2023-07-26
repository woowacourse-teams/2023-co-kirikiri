package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import java.util.List;
import java.util.Optional;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId);

    List<GoalRoom> findAllByStartDateWithGoalRoomRoadmapNode();
}
