package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.member.domain.QMember.member;
import static co.kirikiri.member.domain.QMemberImage.memberImage;
import static co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType.ACCOMPLISHMENT_RATE;
import static co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType.JOINED_ASC;
import static co.kirikiri.roadmap.domain.QRoadmapContent.roadmapContent;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType;
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
                                                                                        final Identifier identifier,
                                                                                        final GoalRoomStatus status) {
        return Optional.ofNullable(selectFrom(goalRoomMember)
                .innerJoin(goalRoomMember.goalRoom, goalRoom)
                .fetchJoin()
                .innerJoin(goalRoom.roadmapContent, roadmapContent)
                .fetchJoin()
                .innerJoin(goalRoomMember.member, member)
                .fetchJoin()
                .where(
                        goalRoom.roadmapContent.roadmapId.eq(roadmapId),
                        member.identifier.eq(identifier),
                        goalRoom.status.eq(status))
                .fetchOne());
    }

    @Override
    public List<GoalRoomMember> findByGoalRoomIdOrderedBySortType(final Long goalRoomId,
                                                                  final GoalRoomMemberSortType sortType) {
        return selectFrom(goalRoomMember)
                .innerJoin(goalRoomMember.member, member)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(goalRoomMember.goalRoom.id.eq(goalRoomId))
                .orderBy(sortCond(sortType))
                .fetch();
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
}
