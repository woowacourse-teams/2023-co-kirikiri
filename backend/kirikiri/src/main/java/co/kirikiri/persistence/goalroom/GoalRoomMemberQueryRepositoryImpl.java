package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberImage.memberImage;
import static co.kirikiri.domain.roadmap.QRoadmapContent.roadmapContent;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.goalroom.dto.GoalRoomMemberSortType;
import com.querydsl.core.types.OrderSpecifier;
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
                        goalRoom.roadmapContent.roadmap.id.eq(roadmapId),
                        member.identifier.eq(identifier),
                        goalRoom.status.eq(status))
                .fetchOne());
    }

    @Override
    public List<GoalRoomMember> findByGoalRoomIdBySortType(final Long goalRoomId,
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
        if (sortType == null || sortType.isAccomplishmentRate()) {
            return goalRoomMember.accomplishmentRate.desc();
        }
        if (sortType.isJoinedASC()) {
            return goalRoomMember.joinedAt.asc();
        }
        return goalRoomMember.joinedAt.desc();
    }
}
