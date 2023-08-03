package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.domain.goalroom.QGoalRoomPendingMember.goalRoomPendingMember;
import static co.kirikiri.domain.goalroom.QGoalRoomRoadmapNode.goalRoomRoadmapNode;
import static co.kirikiri.domain.goalroom.QGoalRoomToDo.goalRoomToDo;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberProfile.memberProfile;
import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapContent.roadmapContent;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.dto.GoalRoomLastValueDto;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GoalRoomQueryRepositoryImpl extends QuerydslRepositorySupporter implements GoalRoomQueryRepository {

    public GoalRoomQueryRepositoryImpl() {
        super(GoalRoom.class);
    }

    @Override
    public Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId) {
        return Optional.ofNullable(selectFrom(goalRoom)
                .innerJoin(goalRoom.roadmapContent, roadmapContent)
                .where(goalRoomIdCond(goalRoomId))
                .fetchJoin()
                .fetchFirst());
    }

    @Override
    public List<GoalRoom> findGoalRoomsWithPendingMembersByRoadmapAndCond(final Roadmap roadmap,
                                                                          final RoadmapGoalRoomsFilterType filterType,
                                                                          final GoalRoomLastValueDto lastValue,
                                                                          final int pageSize) {
        return selectFrom(goalRoom)
                .innerJoin(goalRoom.roadmapContent, roadmapContent)
                .on(roadmapContent.roadmap.eq(roadmap))
                .innerJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .fetchJoin()
                .innerJoin(goalRoomPendingMember.member, member)
                .fetchJoin()
                .innerJoin(member.memberProfile, memberProfile)
                .fetchJoin()
                .where(statusCond(GoalRoomStatus.RECRUITING), lessThanLastValue(lastValue))
                .orderBy(sortCond(filterType))
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<GoalRoom> findAllByStartDateNow() {
        return selectFrom(goalRoom)
                .join(goalRoom.goalRoomRoadmapNodes.values, goalRoomRoadmapNode)
                .where(startDateEqualsToNow())
                .fetch();
    }

    @Override
    public Optional<GoalRoom> findByIdWithTodos(final Long goalRoomId) {
        return Optional.ofNullable(selectFrom(goalRoom)
                .leftJoin(goalRoom.goalRoomToDos.values, goalRoomToDo)
                .where(goalRoomIdCond(goalRoomId))
                .fetchJoin()
                .fetchFirst());
    }

    @Override
    public Optional<GoalRoomMember> findGoalRoomMember(final Long goalRoomId, final Identifier memberIdentifier) {
        return Optional.ofNullable(selectFrom(goalRoomMember)
                .innerJoin(goalRoomMember.goalRoom, goalRoom)
                .where(
                        goalRoomIdCond(goalRoomId),
                        memberIdentifierCond(memberIdentifier))
                .fetchJoin()
                .fetchFirst());
    }

    private BooleanExpression goalRoomIdCond(final Long goalRoomId) {
        return goalRoom.id.eq(goalRoomId);
    }

    private BooleanExpression memberIdentifierCond(final Identifier memberIdentifier) {
        return goalRoomMember.member.identifier.eq(memberIdentifier);
    }

    private BooleanExpression statusCond(final GoalRoomStatus status) {
        return goalRoom.status.eq(status);
    }

    private BooleanExpression lessThanLastValue(final GoalRoomLastValueDto lastValue) {
        if (lastValue == null) {
            return null;
        }
        return roadmap.createdAt.lt(lastValue.getLastCreatedAt());
    }

    private OrderSpecifier<?> sortCond(final RoadmapGoalRoomsFilterType filterType) {
        if (filterType == RoadmapGoalRoomsFilterType.LATEST) {
            return goalRoom.id.desc();
        }
        return goalRoom.goalRoomPendingMembers.values.size().divide(goalRoom.limitedMemberCount.value).desc();
    }

    private BooleanExpression startDateEqualsToNow() {
        return goalRoomRoadmapNode.period.startDate.eq(LocalDate.now());
    }
}