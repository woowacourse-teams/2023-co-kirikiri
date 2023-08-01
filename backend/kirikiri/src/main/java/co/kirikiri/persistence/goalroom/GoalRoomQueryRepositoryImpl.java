package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomPendingMember.goalRoomPendingMember;
import static co.kirikiri.domain.goalroom.QGoalRoomRoadmapNode.goalRoomRoadmapNode;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberProfile.memberProfile;
import static co.kirikiri.domain.roadmap.QRoadmapContent.roadmapContent;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.goalroom.dto.GoalRoomFilterType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

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
    public Page<GoalRoom> findGoalRoomsWithPendingMembersPageByCond(final GoalRoomFilterType filterType,
                                                                    final Pageable pageable) {
        final List<GoalRoom> goalRooms = selectFrom(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .fetchJoin()
                .leftJoin(goalRoomPendingMember.member, member)
                .fetchJoin()
                .leftJoin(member.memberProfile, memberProfile)
                .fetchJoin()
                .where(statusCond(GoalRoomStatus.RECRUITING))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortCond(filterType))
                .fetch();

        final JPAQuery<Long> countQuery = select(goalRoom.count())
                .from(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .leftJoin(goalRoomPendingMember.member, member)
                .leftJoin(member.memberProfile, memberProfile)
                .where(statusCond(GoalRoomStatus.RECRUITING));

        return PageableExecutionUtils.getPage(goalRooms, pageable, countQuery::fetchOne);
    }

    @Override
    public List<GoalRoom> findAllByStartDateNow() {
        return selectFrom(goalRoom)
                .join(goalRoom.goalRoomRoadmapNodes.values, goalRoomRoadmapNode)
                .where(startDateEqualsToNow())
                .fetch();
    }

    private BooleanExpression goalRoomIdCond(final Long goalRoomId) {
        return goalRoom.id.eq(goalRoomId);
    }

    private OrderSpecifier<?> sortCond(final GoalRoomFilterType filterType) {
        if (filterType == GoalRoomFilterType.LATEST) {
            return goalRoom.id.desc();
        }
        return goalRoom.goalRoomPendingMembers.values.size().divide(goalRoom.limitedMemberCount.value).desc();
    }

    private BooleanExpression statusCond(final GoalRoomStatus status) {
        return goalRoom.status.eq(status);
    }

    private BooleanExpression startDateEqualsToNow() {
        return goalRoomRoadmapNode.period.startDate.eq(LocalDate.now());
    }
}
