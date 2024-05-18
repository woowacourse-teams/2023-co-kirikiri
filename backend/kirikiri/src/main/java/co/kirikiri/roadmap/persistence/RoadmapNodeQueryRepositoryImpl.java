package co.kirikiri.roadmap.persistence;

import static co.kirikiri.roadmap.domain.QRoadmapNode.roadmapNode;
import static co.kirikiri.roadmap.domain.QRoadmapNodeImage.roadmapNodeImage;

import co.kirikiri.common.persistence.QuerydslRepositorySupporter;
import co.kirikiri.roadmap.domain.RoadmapNode;
import java.util.Optional;

public class RoadmapNodeQueryRepositoryImpl extends QuerydslRepositorySupporter implements
        RoadmapNodeQueryRepository {

    public RoadmapNodeQueryRepositoryImpl() {
        super(RoadmapNode.class);
    }

    @Override
    public Optional<RoadmapNode> findByIdWithRoadmapNodeImages(final Long id) {
        return Optional.ofNullable(selectFrom(roadmapNode)
                .innerJoin(roadmapNode.roadmapNodeImages.values, roadmapNodeImage)
                .fetchJoin()
                .where(roadmapNode.id.eq(id))
                .fetchOne());
    }
}
