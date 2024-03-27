package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType;
import java.util.List;

public interface GoalRoomPendingMemberQueryRepository {

    List<GoalRoomPendingMember> findByGoalRoomIdOrderedBySortType(final Long goalRoomId,
                                                                  final GoalRoomMemberSortType sortType);
}
