package co.kirikiri.persistence.roadmap;

import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapReview.roadmapReview;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.dto.RoadmapLastValueDto;
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
                                                                                 final RoadmapLastValueDto lastValue,
                                                                                 final int pageSize) {
        return selectFrom(roadmapReview)
                .innerJoin(roadmapReview.member, member)
                .fetchJoin()
                .where(roadmapCond(roadmap), lessThanLastValue(lastValue))
                .limit(pageSize)
                .orderBy(orderByCreatedAtDesc())
                .fetch();
    }

    private BooleanExpression roadmapCond(final Roadmap roadmap) {
        return roadmapReview.roadmap.eq(roadmap);
    }

    private BooleanExpression lessThanLastValue(final RoadmapLastValueDto lastValue) {
        if (lastValue == null) {
            return null;
        }
        return roadmap.createdAt.lt(lastValue.getLastCreatedAt());
    }

    private OrderSpecifier<LocalDateTime> orderByCreatedAtDesc() {
        return roadmapReview.createdAt.desc();
    }
}
