package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoadmapMapper {

    public static RoadmapResponse convertToRoadmapResponse(final Roadmap roadmap, final RoadmapContent content) {
        final RoadmapCategory category = roadmap.getCategory();
        final Member creator = roadmap.getCreator();

        return new RoadmapResponse(
                roadmap.getId(),
                new RoadmapCategoryResponse(category.getId(), category.getName()),
                roadmap.getTitle(),
                roadmap.getIntroduction(),
                new MemberResponse(creator.getId(), creator.getMemberProfile().getNickname()),
                content.getContent(),
                roadmap.getDifficulty().name(),
                roadmap.getRequiredPeriod(),
                convertRoadmapNodeResponse(content.getNodes())
        );
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
