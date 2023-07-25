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
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public Page<Roadmap> findRoadmapPagesByCond(final RoadmapCategory category, final RoadmapFilterType orderType,
                                                final Pageable pageable) {

        return applyPagination(pageable,
                (contentQuery) -> contentQuery.selectFrom(roadmap)
                        .innerJoin(roadmap.category, roadmapCategory)
                        .where(statusCond(RoadmapStatus.CREATED), categoryCond(category))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .orderBy(sortCond(orderType))
                        .fetchJoin(),
                (countQuery) -> countQuery.select(roadmap.count())
                        .from(roadmap)
                        .innerJoin(roadmap.category, roadmapCategory)
                        .where(statusCond(RoadmapStatus.CREATED), categoryCond(category)));
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
