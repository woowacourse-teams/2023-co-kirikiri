package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomPendingMember.goalRoomPendingMember;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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
    public Optional<GoalRoom> findByIdWithMember(final Long id, final Member member) {
        return Optional.ofNullable(selectFrom(goalRoom)
                .innerJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .where(goalRoomIdCond(id).and(memberCond(member)))
                .fetchJoin()
                .fetchFirst());
    }

    @Override
    public Page<GoalRoom> findGoalRoomsPageByMember(final Member member, final Pageable pageable) {
        final List<GoalRoom> goalRooms = selectFrom(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .where(memberCond(member))
                .orderBy(goalRoom.status.stringValue().asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchJoin()
                .fetch();

        final JPAQuery<Long> countQuery = select(goalRoom.count())
                .from(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .where(memberCond(member));

        return PageableExecutionUtils.getPage(goalRooms, pageable, countQuery::fetchOne);
    }

    private BooleanExpression goalRoomIdCond(final Long goalRoomId) {
        return goalRoom.id.eq(goalRoomId);
    }

    private BooleanExpression memberCond(final Member member) {
        return goalRoomPendingMember.member.eq(member);
    }
}
