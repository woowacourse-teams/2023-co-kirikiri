package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.member.domain.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsOrderType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoalRoomQueryRepository {

    Optional<GoalRoom> findGoalRoomByIdWithPessimisticLock(Long goalRoomId);

    Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId);

    Optional<GoalRoom> findByIdWithContentAndTodos(final Long goalRoomId);

    List<GoalRoom> findGoalRoomsByRoadmapAndCond(final Roadmap roadmap,
                                                 final RoadmapGoalRoomsOrderType filterType,
                                                 final Long lastId,
                                                 final int pageSize);

    Optional<GoalRoom> findByIdWithTodos(final Long goalRoomId);

    List<GoalRoom> findByMember(final Member member);

    List<GoalRoom> findByMemberAndStatus(final Member member, final GoalRoomStatus goalRoomStatus);

    Optional<GoalRoom> findByIdWithNodes(final Long goalRoomId);

    List<GoalRoom> findByRoadmap(final Roadmap roadmap);

    List<GoalRoom> findAllRecruitingGoalRoomsByStartDateEarlierThan(final LocalDate startDate);
}
