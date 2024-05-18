package co.kirikiri.roadmap.persistence;

import static co.kirikiri.roadmap.domain.QRoadmapContent.roadmapContent;
import static co.kirikiri.roadmap.domain.QRoadmapNode.roadmapNode;

import co.kirikiri.common.persistence.QuerydslRepositorySupporter;
import co.kirikiri.roadmap.domain.RoadmapContent;
import java.util.Optional;

public class RoadmapContentQueryRepositoryImpl extends QuerydslRepositorySupporter implements
        RoadmapContentQueryRepository {

    public RoadmapContentQueryRepositoryImpl() {
        super(RoadmapContent.class);
    }

    @Override
    public Optional<RoadmapContent> findByIdWithRoadmapNodes(final Long id) {
        return Optional.ofNullable(selectFrom(roadmapContent)
                .innerJoin(roadmapContent.nodes.values, roadmapNode)
                .fetchJoin()
                .where(roadmapContent.id.eq(id))
                .fetchOne());
    }
}
