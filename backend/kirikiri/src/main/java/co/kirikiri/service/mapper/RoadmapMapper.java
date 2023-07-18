package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoadmapMapper {

    public static RoadmapResponse convertToRoadmapResponse(final Roadmap roadmap) {
        final RoadmapCategory category = roadmap.getCategory();
        final Member creator = roadmap.getCreator();
        final RoadmapContent recentContent = roadmap.getRecentContent()
                .orElseThrow(() -> new NoSuchElementException("로드맵의 컨텐츠가 존재하지 않습니다"));

        return new RoadmapResponse(
                roadmap.getId(),
                new RoadmapCategoryResponse(category.getId(), category.getName()),
                roadmap.getTitle(),
                roadmap.getIntroduction(),
                new MemberResponse(creator.getId(), creator.getMemberProfile().getNickname()),
                recentContent.getContent(),
                roadmap.getDifficulty().name(),
                roadmap.getRequiredPeriod(),
                convertRoadmapNodeResponse(recentContent.getNodes())
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
