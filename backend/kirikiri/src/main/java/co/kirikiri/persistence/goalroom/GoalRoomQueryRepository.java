package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.persistence.dto.GoalRoomLastValueDto;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import java.util.List;
import java.util.Optional;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId);

    Optional<GoalRoom> findByIdWithContentAndNodesAndTodos(final Long goalRoomId);

    List<GoalRoom> findGoalRoomsWithPendingMembersByRoadmapAndCond(final Roadmap roadmap,
                                                                   final RoadmapGoalRoomsFilterType filterType,
                                                                   final GoalRoomLastValueDto lastValue,
                                                                   final int pageSize);

    List<GoalRoom> findAllByStartDateNow();

    Optional<GoalRoom> findByIdWithTodos(final Long goalRoomId);

    Optional<GoalRoomMember> findGoalRoomMember(final Long goalRoomId, final Identifier memberIdentifier);

    List<GoalRoom> findByMember(final Member member);

    List<GoalRoom> findByMemberAndStatus(final Member member, final GoalRoomStatus goalRoomStatus);
}
