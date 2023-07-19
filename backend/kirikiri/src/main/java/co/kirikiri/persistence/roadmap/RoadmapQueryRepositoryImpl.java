package co.kirikiri.persistence.roadmap;

import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapCategory.roadmapCategory;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class RoadmapQueryRepositoryImpl extends QuerydslRepositorySupporter implements RoadmapQueryRepository {

    public RoadmapQueryRepositoryImpl() {
        super(Roadmap.class);
    }

    @Override
    public Page<Roadmap> findRoadmapPagesByCond(final RoadmapCategory category, final RoadmapFilterType orderType,
                                                final Pageable pageable) {
        final JPAQuery<Roadmap> roadmapContentQuery =
                selectFrom(roadmap)
                        .innerJoin(roadmap.category, roadmapCategory)
                        .where(statusCond(RoadmapStatus.CREATED), categoryCond(category))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(sortCond(orderType))
                        .fetchJoin();

        final JPAQuery<Long> countQuery = select(roadmap.count())
                .from(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .where(statusCond(RoadmapStatus.CREATED), categoryCond(category));

        return applyPagination(pageable, roadmapContentQuery, countQuery);
    }

    private BooleanExpression statusCond(final RoadmapStatus status) {
        return roadmap.status.eq(status);
    }

    private BooleanExpression categoryCond(final RoadmapCategory category) {
        if (category == null) {
            return null;
        }
        return roadmap.category.eq(category);
    }

    private OrderSpecifier<?> sortCond(final RoadmapFilterType orderType) {
        return roadmap.id.desc();
    }
}
