package co.kirikiri.goalroom.persistence;

import static co.kirikiri.goalroom.domain.QGoalRoomPendingMember.goalRoomPendingMember;
import static co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType.JOINED_DESC;

import co.kirikiri.common.persistence.QuerydslRepositorySupporter;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType;
import com.querydsl.core.types.OrderSpecifier;
import java.util.List;

public class GoalRoomPendingMemberQueryRepositoryImpl extends QuerydslRepositorySupporter
        implements GoalRoomPendingMemberQueryRepository {

    public GoalRoomPendingMemberQueryRepositoryImpl() {
        super(GoalRoomPendingMember.class);
    }

    @Override
    public List<GoalRoomPendingMember> findByGoalRoomIdOrderedBySortType(final Long goalRoomId,
                                                                         final GoalRoomMemberSortType sortType) {
        return selectFrom(goalRoomPendingMember)
                .where(goalRoomPendingMember.goalRoom.id.eq(goalRoomId))
                .orderBy(sortCond(sortType))
                .fetch();
    }

    private OrderSpecifier<?> sortCond(final GoalRoomMemberSortType sortType) {
        if (sortType == JOINED_DESC) {
            return goalRoomPendingMember.joinedAt.desc();
        }
        return goalRoomPendingMember.joinedAt.asc();
    }
}
