package co.kirikiri.persistence.roadmap;

import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapCategory.roadmapCategory;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import java.util.Optional;

public class RoadmapQueryRepositoryImpl extends QuerydslRepositorySupporter implements RoadmapQueryRepository {

    public RoadmapQueryRepositoryImpl() {
        super(Roadmap.class);
    }

    @Override
    public Optional<Roadmap> findRoadmapById(final Long roadmapId) {
        return Optional.ofNullable(selectFrom(roadmap)
                .join(roadmap.creator, member)
                .fetchJoin()
                .join(roadmap.category, roadmapCategory)
                .fetchJoin()
                .where(roadmap.id.eq(roadmapId))
                .fetchOne());
    }

    @Override
    public List<Roadmap> findRoadmapPagesByCond(final RoadmapCategory category, final RoadmapFilterType orderType,
                                                final Long lastRoadmapId, final int pageSize) {

        return selectFrom(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .where(
                        lessThanRoadmapId(lastRoadmapId),
                        statusCond(RoadmapStatus.CREATED),
                        categoryCond(category))
                .limit(pageSize)
                .orderBy(sortCond(orderType))
                .fetchJoin()
                .fetch();
    }

    private BooleanExpression lessThanRoadmapId(final Long lastRoadmapId) {
        if (lastRoadmapId == null) {
            return null;
        }
        return roadmap.id.lt(lastRoadmapId);
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
