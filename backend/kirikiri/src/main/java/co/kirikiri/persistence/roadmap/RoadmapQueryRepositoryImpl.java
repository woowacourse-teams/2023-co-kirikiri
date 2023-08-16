

package co.kirikiri.persistence.roadmap;

import static co.kirikiri.domain.goalroom.QGoalRoom.goalRoom;
import static co.kirikiri.domain.goalroom.QGoalRoomMember.goalRoomMember;
import static co.kirikiri.domain.member.QMember.member;
import static co.kirikiri.domain.roadmap.QRoadmap.roadmap;
import static co.kirikiri.domain.roadmap.QRoadmapCategory.roadmapCategory;
import static co.kirikiri.domain.roadmap.QRoadmapReview.roadmapReview;
import static co.kirikiri.domain.roadmap.QRoadmapTag.roadmapTag;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapStatus;
import co.kirikiri.persistence.QuerydslRepositorySupporter;
import co.kirikiri.persistence.dto.RoadmapOrderType;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
import co.kirikiri.persistence.dto.RoadmapSearchTagName;
import co.kirikiri.persistence.dto.RoadmapSearchTitle;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import java.util.Optional;

public class RoadmapQueryRepositoryImpl extends QuerydslRepositorySupporter implements RoadmapQueryRepository {

    private static final int LIMIT_OFFSET = 1;

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
    public List<Roadmap> findRoadmapsByCategory(final RoadmapCategory category, final RoadmapOrderType orderType,
                                                final Long lastId, final int pageSize) {

        return selectFrom(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .fetchJoin()
                .innerJoin(roadmap.creator, member)
                .fetchJoin()
                .leftJoin(roadmap.tags.values, roadmapTag)
                .where(
                        lessThanLastId(lastId, orderType),
                        statusCond(RoadmapStatus.CREATED),
                        categoryCond(category))
                .limit(pageSize + LIMIT_OFFSET)
                .orderBy(sortCond(orderType))
                .fetch();
    }

    @Override
    public List<Roadmap> findRoadmapsByCond(final RoadmapSearchDto searchRequest, final RoadmapOrderType orderType,
                                            final Long lastId, final int pageSize) {
        return selectFrom(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .fetchJoin()
                .innerJoin(roadmap.creator, member)
                .fetchJoin()
                .leftJoin(roadmap.tags.values, roadmapTag)
                .where(
                        lessThanLastId(lastId, orderType),
                        statusCond(RoadmapStatus.CREATED),
                        titleCond(searchRequest.getTitle()),
                        creatorCond(searchRequest.getCreatorId()),
                        tagCond(searchRequest.getTagName()))
                .limit(pageSize + LIMIT_OFFSET)
                .orderBy(sortCond(orderType))
                .fetch();
    }

    @Override
    public List<Roadmap> findRoadmapsWithCategoryByMemberOrderByLatest(final Member member,
                                                                       final Long lastId,
                                                                       final int pageSize) {
        final RoadmapOrderType orderType = RoadmapOrderType.LATEST;
        return selectFrom(roadmap)
                .innerJoin(roadmap.category, roadmapCategory)
                .fetchJoin()
                .where(
                        creatorCond(member.getId()),
                        lessThanLastId(lastId, orderType))
                .limit(pageSize + LIMIT_OFFSET)
                .orderBy(sortCond(orderType))
                .fetch();
    }

    private BooleanExpression categoryCond(final RoadmapCategory category) {
        if (category == null) {
            return null;
        }
        return roadmap.category.eq(category);
    }

    private BooleanExpression statusCond(final RoadmapStatus status) {
        return roadmap.status.eq(status);
    }

    private BooleanExpression titleCond(final RoadmapSearchTitle title) {
        if (title == null) {
            return null;
        }
        return removeBlank(roadmap.title).containsIgnoreCase(title.value());
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
        return roadmap.tags.values
                .any()
                .name.value
                .equalsIgnoreCase(tagName.value());
    }

    private OrderSpecifier<?> sortCond(final RoadmapOrderType orderType) {
        if (orderType == RoadmapOrderType.GOAL_ROOM_COUNT) {
            return new OrderSpecifier<>(
                    Order.DESC,
                    goalRoomCountCond(goalRoom.roadmapContent.roadmap.id.eq(roadmap.id))
            );
        }
        if (orderType == RoadmapOrderType.PARTICIPANT_COUNT) {
            return new OrderSpecifier<>(
                    Order.DESC,
                    participantCountCond(goalRoomMember.goalRoom.roadmapContent.roadmap.id.eq(roadmap.id))
            );
        }
        if (orderType == RoadmapOrderType.REVIEW_RATE) {
            return new OrderSpecifier<>(
                    Order.DESC,
                    reviewRateCond(roadmapReview.roadmap.id.eq(roadmap.id))
            );
        }
        return roadmap.createdAt.desc();
    }

    private JPAQuery<Long> goalRoomCountCond(final BooleanExpression id) {
        return select(goalRoom.count())
                .from(goalRoom)
                .where(id);
    }

    private JPAQuery<Long> participantCountCond(final BooleanExpression id) {
        return select(goalRoomMember.count())
                .from(goalRoomMember)
                .where(id);
    }

    private JPAQuery<Double> reviewRateCond(final BooleanExpression id) {
        return select(roadmapReview.rate.avg())
                .from(roadmapReview)
                .where(id);
    }

    private BooleanExpression lessThanLastId(final Long lastId, final RoadmapOrderType orderType) {
        if (lastId == null) {
            return null;
        }
        if (orderType == RoadmapOrderType.GOAL_ROOM_COUNT) {
            final NumberPath<Long> goalRoomRoadmapId = goalRoom.roadmapContent.roadmap.id;
            return goalRoomCountCond(goalRoomRoadmapId.eq(roadmap.id))
                    .lt(goalRoomCountCond(goalRoomRoadmapId.eq(lastId)));
        }
        if (orderType == RoadmapOrderType.PARTICIPANT_COUNT) {
            final NumberPath<Long> goalRoomMemberRoadmapId = goalRoomMember.goalRoom.roadmapContent.roadmap.id;
            return participantCountCond(goalRoomMemberRoadmapId.eq(roadmap.id))
                    .lt(participantCountCond(goalRoomMemberRoadmapId.eq(lastId)));
        }
        if (orderType == RoadmapOrderType.REVIEW_RATE) {
            final NumberPath<Long> roadmapReviewRoadmapId = roadmapReview.roadmap.id;
            return reviewRateCond(roadmapReviewRoadmapId.eq(roadmap.id))
                    .lt(reviewRateCond(roadmapReviewRoadmapId.eq(lastId)));
        }
        return roadmap.createdAt.lt(
                select(roadmap.createdAt)
                        .from(roadmap)
                        .where(roadmap.id.eq(lastId))
        );
    }
}
