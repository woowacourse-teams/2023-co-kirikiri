package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.roadmap.QRoadmapContent.roadmapContent;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
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
}
