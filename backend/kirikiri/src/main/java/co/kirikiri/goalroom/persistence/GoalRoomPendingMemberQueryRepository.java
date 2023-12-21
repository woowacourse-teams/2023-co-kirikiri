package co.kirikiri.goalroom.persistence;

import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType;
import java.util.List;

public interface GoalRoomPendingMemberQueryRepository {

    List<GoalRoomPendingMember> findByGoalRoomIdOrderedBySortType(final Long goalRoomId,
                                                                  final GoalRoomMemberSortType sortType);
}
