package co.kirikiri.persistence.goalroom;

import static co.kirikiri.domain.goalroom.QCheckFeed.checkFeed;
import static co.kirikiri.domain.goalroom.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.member.QMemberImage.memberImage;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import java.util.List;

public class CheckFeedQueryRepositoryImpl extends QuerydslRepositorySupporter implements CheckFeedQueryRepository {

    public CheckFeedQueryRepositoryImpl() {
        super(CheckFeed.class);
    }

    @Override
    public List<CheckFeed> findByGoalRoomRoadmapNodeWithGoalRoomMemberAndMemberImage(
            final GoalRoomRoadmapNode goalRoomRoadmapNode) {
        return selectFrom(checkFeed)
                .innerJoin(checkFeed.goalRoomMember, goalRoomMember)
                .fetchJoin()
                .innerJoin(goalRoomMember.member, member)
                .fetchJoin()
                .innerJoin(member.image, memberImage)
                .fetchJoin()
                .where(checkFeed.goalRoomRoadmapNode.eq(goalRoomRoadmapNode))
                .orderBy(checkFeed.createdAt.desc())
                .fetch();
    }
}
