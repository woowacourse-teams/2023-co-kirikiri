package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QCheckFeed.checkFeed;
import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberImage.memberImage;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import java.util.Optional;

public class CheckFeedQueryRepositoryImpl extends QuerydslRepositorySupporter implements CheckFeedQueryRepository {

    public CheckFeedQueryRepositoryImpl() {
        super(CheckFeed.class);
    }

    @Override
    public List<CheckFeed> findByGoalRoomRoadmapNodeAndGoalRoomStatusWithMemberAndMemberImage(
            final Optional<GoalRoomRoadmapNode> goalRoomRoadmapNode, final GoalRoomStatus status) {
        return selectFrom(checkFeed)
                .innerJoin(checkFeed.goalRoomMember, goalRoomMember)
                .fetchJoin()
                .innerJoin(goalRoomMember.member, member)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(nodeAndStatusCond(goalRoomRoadmapNode, status))
                .orderBy(checkFeed.createdAt.desc())
                .fetch();
    }

    @Override
    public List<CheckFeed> findByGoalRoomRoadmapNodeAndGoalRoomStatus(
            final Optional<GoalRoomRoadmapNode> currentGoalRoomRoadmapNode, final GoalRoomStatus status) {
        return selectFrom(checkFeed)
                .innerJoin(checkFeed.goalRoomMember, goalRoomMember)
                .fetchJoin()
                .innerJoin(goalRoomMember.goalRoom, goalRoom)
                .fetchJoin()
                .where(nodeAndStatusCond(currentGoalRoomRoadmapNode, status))
                .orderBy(checkFeed.createdAt.desc())
                .fetch();
    }

    private BooleanExpression nodeAndStatusCond(final Optional<GoalRoomRoadmapNode> node, final GoalRoomStatus status) {
        if (status != GoalRoomStatus.RUNNING) {
            return null;
        }
        if (node.isEmpty()) {
            return checkFeed.id.eq(-1L);
        }
        return checkFeed.goalRoomRoadmapNode.eq(node.get());
    }
}
