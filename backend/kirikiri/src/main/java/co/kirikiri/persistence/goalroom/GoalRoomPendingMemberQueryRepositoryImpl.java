package co.kirikiri.persistence.goalroom;

import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType;
import com.querydsl.core.types.OrderSpecifier;

import java.util.List;

import static co.kirikiri.domain.goalroom.QGoalRoomPendingMember.goalRoomPendingMember;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberImage.memberImage;
import static co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType.JOINED_DESC;

public class GoalRoomPendingMemberQueryRepositoryImpl extends QuerydslRepositorySupporter
        implements GoalRoomPendingMemberQueryRepository {

    public GoalRoomPendingMemberQueryRepositoryImpl() {
        super(GoalRoomPendingMember.class);
    }

    @Override
    public List<GoalRoomPendingMember> findByGoalRoomIdOrderedBySortType(final Long goalRoomId,
                                                                         final GoalRoomMemberSortType sortType) {
        return selectFrom(goalRoomPendingMember)
                .innerJoin(goalRoomPendingMember.member, member)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
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
