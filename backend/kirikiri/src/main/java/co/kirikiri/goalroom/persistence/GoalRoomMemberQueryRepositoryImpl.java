package co.kirikiri.goalroom.persistence;

import static co.kirikiri.goalroom.domain.QGoalRoom.goalRoom;
import static co.kirikiri.goalroom.domain.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType.ACCOMPLISHMENT_RATE;
import static co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType.JOINED_ASC;

import co.kirikiri.common.persistence.QuerydslRepositorySupporter;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomStatus;
import co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import java.util.Optional;

public class GoalRoomMemberQueryRepositoryImpl extends QuerydslRepositorySupporter implements
        GoalRoomMemberQueryRepository {

    public GoalRoomMemberQueryRepositoryImpl() {
        super(GoalRoomMember.class);
    }

    @Override
    public Optional<GoalRoomMember> findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(final Long roadmapId,
                                                                                        final Long memberId,
                                                                                        final GoalRoomStatus status) {
        return Optional.ofNullable(selectFrom(goalRoomMember)
                .innerJoin(goalRoomMember.goalRoom, goalRoom)
                .fetchJoin()
                .where(
                        memberIdCond(memberId),
                        goalRoom.status.eq(status))
                .fetchOne());
    }

    @Override
    public List<GoalRoomMember> findByGoalRoomIdOrderedBySortType(final Long goalRoomId,
                                                                  final GoalRoomMemberSortType sortType) {
        return selectFrom(goalRoomMember)
                .where(goalRoomMember.goalRoom.id.eq(goalRoomId))
                .orderBy(sortCond(sortType))
                .fetch();
    }

    @Override
    public Optional<GoalRoomMember> findByGoalRoomIdAndMemberId(final Long goalRoomId, final Long memberId) {
        return Optional.ofNullable(selectFrom(goalRoomMember)
                .innerJoin(goalRoomMember.goalRoom, goalRoom)
                .fetchJoin()
                .where(
                        goalRoomIdCond(goalRoomId),
                        memberIdCond(memberId))
                .fetchFirst());
    }

    private OrderSpecifier<?> sortCond(final GoalRoomMemberSortType sortType) {
        if (sortType == null || sortType == ACCOMPLISHMENT_RATE) {
            return goalRoomMember.accomplishmentRate.desc();
        }
        if (sortType == JOINED_ASC) {
            return goalRoomMember.joinedAt.asc();
        }
        return goalRoomMember.joinedAt.desc();
    }

    private BooleanExpression goalRoomIdCond(final Long goalRoomId) {
        return goalRoom.id.eq(goalRoomId);
    }

    private BooleanExpression memberIdCond(final Long memberId) {
        return goalRoomMember.memberId.eq(memberId);
    }
}
