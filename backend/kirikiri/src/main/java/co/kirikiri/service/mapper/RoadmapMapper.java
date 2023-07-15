package co.kirikiri.service.mapper;

import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapDetailResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.SingleRoadmapResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoadmapMapper {

    public static SingleRoadmapResponse convertSingleRoadmapResponse(final Roadmap roadmap) {

        final RoadmapDetailResponse detailResponse = new RoadmapDetailResponse(
                new RoadmapCategoryResponse(roadmap.getCategory().getId(), roadmap.getCategory().getName()),
                roadmap.getTitle(),
                roadmap.getIntroduction(),
                new MemberResponse(roadmap.getCreator().getId(), roadmap.getCreator().getMemberProfile().getNickname()),
                roadmap.getRecentContent().getContent(),
                roadmap.getDifficulty().name(),
                roadmap.getRequiredPeriod(),
                convertRoadmapNodeResponse(roadmap.getRecentContent().getNodes())
        );

        return new SingleRoadmapResponse(detailResponse);
    }

    private static List<RoadmapNodeResponse> convertRoadmapNodeResponse(final List<RoadmapNode> nodes) {
        return nodes.stream()
                .map(RoadmapMapper::convertNode)
                .toList();
    }

    private static RoadmapNodeResponse convertNode(final RoadmapNode node) {
        final List<String> images = node.getImages()
                .stream()
                .map(RoadmapNodeImage::getServerFilePath)
                .toList();

        return new RoadmapNodeResponse(node.getTitle(), node.getContent(), images);
    }
}
