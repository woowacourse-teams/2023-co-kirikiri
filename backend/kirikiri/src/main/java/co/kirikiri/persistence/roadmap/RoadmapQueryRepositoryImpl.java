package co.kirikiri.persistence.roadmap;

import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapCategory.roadmapCategory;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.roadmap.dto.RoadmapFilterType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class RoadmapQueryRepositoryImpl extends QuerydslRepositorySupporter implements RoadmapQueryRepository {

    public RoadmapQueryRepositoryImpl() {
        super(Roadmap.class);
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

    @Override
    public List<Roadmap> findRoadmapsWithCategoryByMemberOrderByIdDesc(final Member member, final Long lastValue,
                                                                       final int pageSize) {
        return selectFrom(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .fetchJoin()
                .where(memberCond(member), lessThanLastValue(lastValue))
                .limit(pageSize)
                .orderBy(orderByIdDesc())
                .fetch();
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

    private BooleanExpression memberCond(final Member member) {
        return roadmap.creator.eq(member);
    }

    private BooleanExpression lessThanLastValue(final Long lastValue) {
        if (lastValue == null) {
            return null;
        }
        return roadmap.id.lt(lastValue);
    }

    private OrderSpecifier<Long> orderByIdDesc() {
        return roadmap.id.desc();
    }
}
