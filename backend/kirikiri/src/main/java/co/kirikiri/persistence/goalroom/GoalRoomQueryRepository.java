package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import java.util.List;
import java.util.Optional;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId);

    Optional<GoalRoom> findByIdWithContentAndTodos(final Long goalRoomId);

    List<GoalRoom> findGoalRoomsWithPendingMembersByRoadmapAndCond(final Roadmap roadmap,
                                                                   final RoadmapGoalRoomsFilterType filterType,
                                                                   final Long lastId,
                                                                   final int pageSize);

    List<GoalRoom> findAllByStartDateNow();

    Optional<GoalRoom> findByIdWithTodos(final Long goalRoomId);

    List<GoalRoom> findByMember(final Member member);

    List<GoalRoom> findByMemberAndStatus(final Member member, final GoalRoomStatus goalRoomStatus);

    Optional<GoalRoom> findByIdWithNodes(final Long goalRoomId);

    List<GoalRoom> findByRoadmap(final Roadmap roadmap);
}
