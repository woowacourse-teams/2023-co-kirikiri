package co.kirikiri.roadmap.persistence;

import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapReview;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.LocalDateTime;
import java.util.List;

import static co.kirikiri.roadmap.domain.QRoadmapReview.roadmapReview;

public class RoadmapReviewQueryRepositoryImpl extends QuerydslRepositorySupporter implements
        RoadmapReviewQueryRepository {

    public RoadmapReviewQueryRepositoryImpl() {
        super(RoadmapReview.class);
    }

    @Override
    public List<RoadmapReview> findRoadmapReviewByRoadmapOrderByLatest(final Roadmap roadmap,
                                                                       final Long lastId,
                                                                       final int pageSize) {
        return selectFrom(roadmapReview)
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
