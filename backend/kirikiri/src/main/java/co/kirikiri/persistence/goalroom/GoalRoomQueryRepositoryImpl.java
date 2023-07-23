package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomPendingMember.goalRoomPendingMember;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class GoalRoomQueryRepositoryImpl implements GoalRoomQueryRepository {

    private final JPAQueryFactory factory;

    public GoalRoomQueryRepositoryImpl(final EntityManager em) {
        this.factory = new JPAQueryFactory(em);
    }

    @Override
    public Page<GoalRoom> findGoalRoomsPageByMember(final Member member, final Pageable pageable) {
        final List<GoalRoom> goalRooms = factory
                .selectFrom(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .where(goalRoomPendingMember.member.eq(member))
                .orderBy(goalRoom.status.stringValue().asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchJoin()
                .fetch();

        final JPAQuery<Long> countQuery = factory
                .select(goalRoom.count())
                .from(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .where(goalRoomPendingMember.member.eq(member));

        return PageableExecutionUtils.getPage(goalRooms, pageable, countQuery::fetchOne);
    }
}
