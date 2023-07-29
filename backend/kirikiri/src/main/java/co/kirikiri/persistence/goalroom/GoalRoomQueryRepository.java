package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.persistence.goalroom.dto.GoalRoomFilterType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId);

    Page<GoalRoom> findGoalRoomsWithPendingMembersPageByCond(final GoalRoomFilterType filterType,
                                                             final Pageable pageable);

    List<GoalRoom> findAllByStartDateWithGoalRoomRoadmapNode();
}
