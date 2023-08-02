package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomPendingMember.goalRoomPendingMember;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberProfile.memberProfile;
import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapContent.roadmapContent;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import java.util.Optional;

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

    private BooleanExpression goalRoomIdCond(final Long goalRoomId) {
        return goalRoom.id.eq(goalRoomId);
    }

    @Override
    public List<GoalRoom> findGoalRoomsWithPendingMembersPageByCond(final Roadmap roadmap,
                                                                    final RoadmapGoalRoomsFilterType filterType,
                                                                    final Long lastValue, final int pageSize) {
        return selectFrom(goalRoom)
                .innerJoin(goalRoom.roadmapContent, roadmapContent)
                .on(roadmapContent.roadmap.eq(roadmap))
                .innerJoin(goalRoom.goalRoomPendingMembers.values, goalRoomPendingMember)
                .fetchJoin()
                .innerJoin(goalRoomPendingMember.member, member)
                .fetchJoin()
                .innerJoin(member.memberProfile, memberProfile)
                .fetchJoin()
                .where(statusCond(GoalRoomStatus.RECRUITING), lessThanLastValue(lastValue))
                .orderBy(sortCond(filterType))
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression statusCond(final GoalRoomStatus status) {
        return goalRoom.status.eq(status);
    }

    private BooleanExpression lessThanLastValue(final Long lastValue) {
        if (lastValue == null) {
            return null;
        }
        return roadmap.id.lt(lastValue);
    }

    private OrderSpecifier<?> sortCond(final RoadmapGoalRoomsFilterType filterType) {
        if (filterType == RoadmapGoalRoomsFilterType.LATEST) {
            return goalRoom.id.desc();
        }
        return goalRoom.goalRoomPendingMembers.values.size().divide(goalRoom.limitedMemberCount.value).desc();
    }
}
