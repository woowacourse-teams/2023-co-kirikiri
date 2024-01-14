package co.kirikiri.goalroom.persistence;

import static co.kirikiri.goalroom.domain.QGoalRoom.goalRoom;
import static co.kirikiri.goalroom.domain.QGoalRoomRoadmapNode.goalRoomRoadmapNode;

import co.kirikiri.common.persistence.QuerydslRepositorySupporter;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomStatus;
import co.kirikiri.goalroom.persistence.dto.RoadmapGoalRoomsOrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class GoalRoomQueryRepositoryImpl extends QuerydslRepositorySupporter implements GoalRoomQueryRepository {

    private static final int LIMIT_OFFSET = 1;

    public GoalRoomQueryRepositoryImpl() {
        super(GoalRoom.class);
    }

    @Override
    public Optional<GoalRoom> findGoalRoomByIdWithPessimisticLock(final Long goalRoomId) {
        return Optional.ofNullable(selectFrom(goalRoom)
                .where(goalRoom.id.eq(goalRoomId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne());
    }

    @Override
    public List<GoalRoom> findGoalRoomsByRoadmapContentIdAndCond(final Long roadmapContentId,
                                                                 final RoadmapGoalRoomsOrderType orderType,
                                                                 final Long lastId,
                                                                 final int pageSize) {
        return selectFrom(goalRoom)
                .where(
                        statusCond(orderType),
                        lessThanLastId(lastId, orderType),
                        roadmapContentIdCond(roadmapContentId))
                .limit(pageSize + LIMIT_OFFSET)
                .orderBy(sortCond(orderType))
                .fetch();
    }

    @Override
    public Optional<GoalRoom> findByIdWithNodes(final Long goalRoomId) {
        return Optional.ofNullable(selectFrom(goalRoom)
                .innerJoin(goalRoom.goalRoomRoadmapNodes.values, goalRoomRoadmapNode)
                .fetchJoin()
                .where(goalRoomIdCond(goalRoomId))
                .fetchOne());
    }

    @Override
    public List<GoalRoom> findAllRecruitingGoalRoomsByStartDateEarlierThan(final LocalDate date) {
        return selectFrom(goalRoom)
                .where(statusCond(GoalRoomStatus.RECRUITING))
                .where(equalOrEarlierStartDateThan(date))
                .fetch();
    }

    private BooleanExpression goalRoomIdCond(final Long goalRoomId) {
        return goalRoom.id.eq(goalRoomId);
    }

    private BooleanExpression statusCond(final GoalRoomStatus status) {
        return goalRoom.status.eq(status);
    }

    private BooleanExpression statusCond(final RoadmapGoalRoomsOrderType orderType) {
        if (orderType == RoadmapGoalRoomsOrderType.CLOSE_TO_DEADLINE) {
            return statusCond(GoalRoomStatus.RECRUITING);
        }
        return null;
    }

    private OrderSpecifier<?> sortCond(final RoadmapGoalRoomsOrderType orderType) {
        if (orderType == RoadmapGoalRoomsOrderType.CLOSE_TO_DEADLINE) {
            return goalRoom.startDate.asc();
        }
        return goalRoom.createdAt.desc();
    }

    private BooleanExpression lessThanLastId(final Long lastId, final RoadmapGoalRoomsOrderType orderType) {
        if (lastId == null) {
            return null;
        }
        if (orderType == RoadmapGoalRoomsOrderType.CLOSE_TO_DEADLINE) {
            return select(goalRoom.startDate)
                    .from(goalRoom)
                    .where(goalRoom.id.eq(lastId))
                    .lt(goalRoom.startDate);
        }
        return goalRoom.createdAt.lt(
                select(goalRoom.createdAt)
                        .from(goalRoom)
                        .where(goalRoom.id.eq(lastId))
        );
    }

    private BooleanExpression roadmapContentIdCond(final Long roadmapContentId) {
        return goalRoom.roadmapContentId.eq(roadmapContentId);
    }

    private BooleanExpression equalOrEarlierStartDateThan(final LocalDate date) {
        return goalRoom.startDate.loe(date);
    }
}
