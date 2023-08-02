package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import java.util.List;
import java.util.Optional;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId);

    List<GoalRoom> findGoalRoomsWithPendingMembersPageByCond(final Roadmap roadmap,
                                                             final RoadmapGoalRoomsFilterType filterType,
                                                             final Long lastValue, final int pageSize);
}
