package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.roadmap.dto.GoalRoomFilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GoalRoomQueryRepository {

    Page<GoalRoom> findGoalRoomsPageByCond(GoalRoomFilterType filterType, Pageable pageable);
}
