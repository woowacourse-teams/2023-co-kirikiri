package co.kirikiri.persistence.roadmap;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapCategory.roadmapCategory;
import static co.kirikiri.domain.roadmap.QRoadmapContent.roadmapContent;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class RoadmapQueryRepositoryImpl extends QuerydslRepositorySupporter implements RoadmapQueryRepository {

    public RoadmapQueryRepositoryImpl() {
        super(Roadmap.class);
    }

    @Override
    public Optional<Roadmap> findByGoalRoomId(final Long goalRoomId) {
        return Optional.ofNullable(selectFrom(roadmap)
                .innerJoin(roadmap, roadmapContent.roadmap)
                .innerJoin(roadmapContent, goalRoom.roadmapContent)
                .where(goalRoom.id.eq(goalRoomId))
                .fetchJoin()
                .fetchFirst());
    }

    @Override
    public Page<Roadmap> findRoadmapPagesByCond(final RoadmapCategory category, final RoadmapFilterType orderType,
                                                final Pageable pageable) {
        final List<Roadmap> roadmaps = selectFrom(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .where(statusCond(RoadmapStatus.CREATED), categoryCond(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(sortCond(orderType))
                .fetchJoin()
                .fetch();

        final JPAQuery<Long> countQuery = select(roadmap.count())
                .from(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .where(statusCond(RoadmapStatus.CREATED), categoryCond(category));

        return PageableExecutionUtils.getPage(roadmaps, pageable, countQuery::fetchOne);
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
