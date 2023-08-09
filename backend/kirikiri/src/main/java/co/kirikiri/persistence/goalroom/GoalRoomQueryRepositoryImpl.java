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
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.Member;
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

    private static final int LIMIT_OFFSET = 1;

    public GoalRoomQueryRepositoryImpl() {
        super(GoalRoom.class);
    }

    @Override
    public Optional<GoalRoom> findByIdWithRoadmapContent(final Long goalRoomId) {
        return Optional.ofNullable(selectFrom(goalRoom)
                .innerJoin(goalRoom.roadmapContent, roadmapContent)
                .fetchJoin()
                .where(goalRoomIdCond(goalRoomId))
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
                .limit(pageSize + LIMIT_OFFSET)
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
                .fetchJoin()
                .where(goalRoomIdCond(goalRoomId))
                .fetchFirst());
    }

    @Override
    public Optional<GoalRoom> findByIdWithContentAndTodos(final Long goalRoomId) {
        return Optional.ofNullable(selectFrom(goalRoom)
                .innerJoin(goalRoom.roadmapContent, roadmapContent)
                .fetchJoin()
                .innerJoin(goalRoom.goalRoomToDos.values, goalRoomToDo)
                .fetchJoin()
                .where(goalRoomIdCond(goalRoomId))
                .fetchOne());
    }

    @Override
    public Optional<GoalRoom> findByIdWithNodes(final Long goalRoomId) {
        return Optional.ofNullable(selectFrom(goalRoom)
                .innerJoin(goalRoom.goalRoomRoadmapNodes.values, goalRoomRoadmapNode)
                .fetchJoin()
                .where(goalRoomIdCond(goalRoomId))
                .fetchOne());
    }

    private BooleanExpression goalRoomIdCond(final Long goalRoomId) {
        return goalRoom.id.eq(goalRoomId);
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

    @Override
    public List<GoalRoom> findByMember(final Member member) {
        return selectFrom(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .leftJoin(goalRoom.goalRoomMembers.values, goalRoomMember)
                .where(goalRoomPendingMember.member.eq(member)
                        .or(goalRoomMember.member.eq(member)))
                .fetch();
    }

    @Override
    public List<GoalRoom> findByMemberAndStatus(final Member member, final GoalRoomStatus goalRoomStatus) {
        return selectFrom(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .leftJoin(goalRoom.goalRoomMembers.values, goalRoomMember)
                .where(goalRoomPendingMember.member.eq(member)
                        .or(goalRoomMember.member.eq(member)))
                .where(statusCond(goalRoomStatus))
                .fetch();
    }

    @Override
    public List<GoalRoom> findByRoadmap(final Roadmap roadmap) {
        return selectFrom(goalRoom)
                .innerJoin(goalRoom.roadmapContent, roadmapContent)
                .on(roadmapContent.roadmap.eq(roadmap))
                .fetch();
    }
}
