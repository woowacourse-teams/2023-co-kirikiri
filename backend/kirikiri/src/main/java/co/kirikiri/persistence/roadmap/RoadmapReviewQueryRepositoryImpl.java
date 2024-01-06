package co.kirikiri.persistence.roadmap;

import static co.kirikiri.domain.roadmap.QRoadmapReview.roadmapReview;
import static co.kirikiri.member.domain.QMember.member;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.List;

public class RoadmapReviewQueryRepositoryImpl extends QuerydslRepositorySupporter implements
        RoadmapReviewQueryRepository {

    public RoadmapReviewQueryRepositoryImpl() {
        super(RoadmapReview.class);
    }

    @Override
    public List<RoadmapReview> findRoadmapReviewWithMemberByRoadmapOrderByLatest(final Roadmap roadmap,
                                                                                 final Long lastId,
                                                                                 final int pageSize) {
        return selectFrom(roadmapReview)
                .innerJoin(roadmapReview.member, member)
                .fetchJoin()
                .where(roadmapCond(roadmap), lessThanLastId(lastId))
                .limit(pageSize)
                .orderBy(orderByCreatedAtDesc())
                .fetch();
    }

    private BooleanExpression roadmapCond(final Roadmap roadmap) {
        return roadmapReview.roadmap.eq(roadmap);
    }

    private BooleanExpression lessThanLastId(final Long lastId) {
        if (lastId == null) {
            return null;
        }
        return roadmapReview.createdAt.lt(
                select(roadmapReview.createdAt).from(roadmapReview).where(roadmapReview.id.eq(lastId))
        );
    }

    private OrderSpecifier<LocalDateTime> orderByCreatedAtDesc() {
        return roadmapReview.createdAt.desc();
    }
}
