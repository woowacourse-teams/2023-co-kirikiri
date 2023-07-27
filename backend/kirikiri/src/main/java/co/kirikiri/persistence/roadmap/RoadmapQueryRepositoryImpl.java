package co.kirikiri.persistence.roadmap;

import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapCategory.roadmapCategory;
import static co.kirikiri.domain.roadmap.QRoadmapTag.roadmapTag;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.dto.RoadmapFilterType;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
import co.kirikiri.persistence.dto.RoadmapSearchTagName;
import co.kirikiri.persistence.dto.RoadmapSearchTitle;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import java.util.List;
import java.util.Optional;

public class RoadmapQueryRepositoryImpl extends QuerydslRepositorySupporter implements RoadmapQueryRepository {

    public RoadmapQueryRepositoryImpl() {
        super(Roadmap.class);
    }

    @Override
    public Optional<Roadmap> findRoadmapById(final Long roadmapId) {
        return Optional.ofNullable(selectFrom(roadmap)
                .innerJoin(roadmap.creator, member)
                .fetchJoin()
                .innerJoin(roadmap.category, roadmapCategory)
                .fetchJoin()
                .leftJoin(roadmap.tags.values, roadmapTag)
                .where(roadmap.id.eq(roadmapId))
                .fetchOne());
    }

    @Override
    public List<Roadmap> findRoadmapsByCond(final RoadmapCategory category, final RoadmapFilterType orderType,
                                            final Long lastValue, final int pageSize) {

        return selectFrom(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .fetchJoin()
                .innerJoin(roadmap.creator, member)
                .fetchJoin()
                .leftJoin(roadmap.tags.values, roadmapTag)
                .where(
                        lessThanLastValue(lastValue),
                        statusCond(RoadmapStatus.CREATED),
                        categoryCond(category))
                .limit(pageSize)
                .orderBy(sortCond(orderType))
                .fetch();
    }

    @Override
    public List<Roadmap> searchRoadmaps(final RoadmapFilterType orderType, final RoadmapSearchDto searchRequest,
                                        final Long lastValue, final int pageSize) {
        return selectFrom(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .fetchJoin()
                .innerJoin(roadmap.creator, member)
                .fetchJoin()
                .leftJoin(roadmap.tags.values, roadmapTag)
                .where(
                        lessThanLastValue(lastValue),
                        statusCond(RoadmapStatus.CREATED),
                        titleCond(searchRequest.getTitle()),
                        creatorCond(searchRequest.getCreatorId()),
                        tagCond(searchRequest.getTagName()))
                .limit(pageSize)
                .orderBy(sortCond(orderType))
                .fetch();
    }

    // TODO 최신순에서만 no-offset 적용이 되는 코드
    private BooleanExpression lessThanLastValue(final Long lastValue) {
        if (lastValue == null) {
            return null;
        }
        return roadmap.id.lt(lastValue);
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

    private BooleanExpression titleCond(final RoadmapSearchTitle title) {
        if (title == null) {
            return null;
        }
        return removeBlank(roadmap.title).containsIgnoreCase(title.getValue());
    }

    private StringExpression removeBlank(final StringExpression field) {
        return Expressions.stringTemplate("REPLACE({0}, ' ', '')", field);
    }

    private BooleanExpression creatorCond(final Long creatorId) {
        if (creatorId == null) {
            return null;
        }
        return roadmap.creator.id.eq(creatorId);
    }

    private BooleanExpression tagCond(final RoadmapSearchTagName tagName) {
        if (tagName == null) {
            return null;
        }

        return roadmap.tags.values.any().name.value.equalsIgnoreCase(tagName.getValue());
    }

    private OrderSpecifier<?> sortCond(final RoadmapFilterType orderType) {
        return roadmap.id.desc();
    }
}
