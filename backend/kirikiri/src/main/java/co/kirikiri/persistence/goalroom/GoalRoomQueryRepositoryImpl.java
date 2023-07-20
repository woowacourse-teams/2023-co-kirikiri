package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomPendingMember.goalRoomPendingMember;
import static co.kirikiri.domain.goalroom.QGoalRoomRoadmapNode.goalRoomRoadmapNode;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.roadmap.dto.GoalRoomFilterType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public Page<GoalRoom> findGoalRoomsPageByCond(final GoalRoomFilterType filterType, final Pageable pageable) {
        final List<GoalRoom> goalRooms = factory
                .selectFrom(goalRoom)
                .leftJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .leftJoin(goalRoom.goalRoomRoadmapNodes.values, goalRoomRoadmapNode)
                .where(statusCond(GoalRoomStatus.RECRUITING))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortCond(filterType), goalRoomRoadmapNode.startDate.asc())
                .fetchJoin()
                .fetch();

        final JPAQuery<Long> countQuery = factory
                .select(goalRoom.count())
                .from(goalRoom)
                .leftJoin(goalRoom.goalRoomRoadmapNodes.values, goalRoomRoadmapNode)
                .where(statusCond(GoalRoomStatus.RECRUITING));

        return PageableExecutionUtils.getPage(goalRooms, pageable, countQuery::fetchOne);
    }

    private BooleanExpression statusCond(final GoalRoomStatus status) {
        return goalRoom.status.eq(status);
    }

    private OrderSpecifier<?> sortCond(final GoalRoomFilterType filterType) {
        if (filterType == GoalRoomFilterType.LATEST) {
            return goalRoom.id.desc();
        }
        return goalRoom.limitedMemberCount.divide(goalRoom.goalRoomPendingMembers.values.size()).asc();
    }
}
