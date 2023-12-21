package co.kirikiri.goalroom.persistence;

import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomStatus;
import co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType;
import java.util.List;
import java.util.Optional;

public interface GoalRoomMemberQueryRepository {

    Optional<GoalRoomMember> findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(final Long roadmapId,
                                                                                 final Long memberId,
                                                                                 final GoalRoomStatus status);

    List<GoalRoomMember> findByGoalRoomIdOrderedBySortType(final Long goalRoomId,
                                                           final GoalRoomMemberSortType sortType);

    Optional<GoalRoomMember> findByGoalRoomIdAndMemberId(final Long goalRoomId, final Long memberId);
}
