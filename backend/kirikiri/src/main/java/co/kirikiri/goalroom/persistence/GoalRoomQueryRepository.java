package co.kirikiri.goalroom.persistence;

import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomStatus;
import co.kirikiri.goalroom.persistence.dto.RoadmapGoalRoomsOrderType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findGoalRoomByIdWithPessimisticLock(Long goalRoomId);

    List<GoalRoom> findGoalRoomsByRoadmapContentIdAndCond(final Long roadmapContentId,
                                                          final RoadmapGoalRoomsOrderType filterType,
                                                          final Long lastId,
                                                          final int pageSize);

    List<GoalRoom> findByMemberId(final Long memberId);

    List<GoalRoom> findByMemberAndStatus(final Long memberId, final GoalRoomStatus goalRoomStatus);

    Optional<GoalRoom> findByIdWithNodes(final Long goalRoomId);

    List<GoalRoom> findAllRecruitingGoalRoomsByStartDateEarlierThan(final LocalDate startDate);
}
