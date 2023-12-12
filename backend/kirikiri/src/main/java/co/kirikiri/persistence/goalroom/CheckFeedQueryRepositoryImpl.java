package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QCheckFeed.checkFeed;
import static co.kirikiri.domain.goalroom.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.member.domain.QMember.member;
import static co.kirikiri.member.domain.QMemberImage.memberImage;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;

public class CheckFeedQueryRepositoryImpl extends QuerydslRepositorySupporter implements CheckFeedQueryRepository {

    public CheckFeedQueryRepositoryImpl() {
        super(CheckFeed.class);
    }

    @Override
    public List<CheckFeed> findByRunningGoalRoomRoadmapNodeWithMemberAndMemberImage(
            final GoalRoomRoadmapNode goalRoomRoadmapNode) {
        return selectFrom(checkFeed)
                .innerJoin(checkFeed.goalRoomMember, goalRoomMember)
                .fetchJoin()
                .innerJoin(goalRoomMember.member, member)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(nodeCond(goalRoomRoadmapNode))
                .orderBy(checkFeed.createdAt.desc())
                .fetch();
    }

    @Override
    public List<CheckFeed> findByRunningGoalRoomRoadmapNode(
            final GoalRoomRoadmapNode currentGoalRoomRoadmapNode) {
        return selectFrom(checkFeed)
                .innerJoin(checkFeed.goalRoomMember, goalRoomMember)
                .fetchJoin()
                .innerJoin(goalRoomMember.member, member)
                .fetchJoin()
                .where(nodeCond(currentGoalRoomRoadmapNode))
                .orderBy(checkFeed.createdAt.desc())
                .fetch();
    }

    @Override
    public List<CheckFeed> findByGoalRoomWithMemberAndMemberImage(final GoalRoom goalRoom) {
        return selectFrom(checkFeed)
                .innerJoin(checkFeed.goalRoomMember, goalRoomMember)
                .fetchJoin()
                .innerJoin(goalRoomMember.member, member)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(goalRoomCond(goalRoom))
                .orderBy(checkFeed.createdAt.desc())
                .fetch();
    }

    private BooleanExpression nodeCond(final GoalRoomRoadmapNode node) {
        return checkFeed.goalRoomRoadmapNode.eq(node);
    }

    private BooleanExpression goalRoomCond(final GoalRoom goalRoom) {
        return goalRoomMember.goalRoom.eq(goalRoom);
    }
}
