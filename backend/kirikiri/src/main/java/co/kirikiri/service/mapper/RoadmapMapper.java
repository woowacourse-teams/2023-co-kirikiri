package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoadmapMapper {

    public static RoadmapSaveDto convertToRoadmapSaveDto(final RoadmapSaveRequest request) {
        final List<RoadmapNodeSaveDto> roadmapNodes = request.roadmapNodes().stream()
                .map(co.kirikiri.service.mapper.RoadmapMapper::convertToRoadmapNodesSaveDto)
                .toList();
        return new RoadmapSaveDto(request.categoryId(), request.title(), request.introduction(), request.content(),
                request.difficulty(), request.requiredPeriod(), roadmapNodes);
    }

    private static RoadmapNodeSaveDto convertToRoadmapNodesSaveDto(final RoadmapNodeSaveRequest request) {
        return new RoadmapNodeSaveDto(request.title(), request.content());

    }

    public static RoadmapFilterType convertRoadmapOrderType(final RoadmapFilterTypeDto filterType) {
        if (filterType == null) {
            return RoadmapFilterType.LATEST;
        }
        try {
            return RoadmapFilterType.valueOf(filterType.name());
        } catch (final IllegalArgumentException e) {
            throw new NotFoundException("존재하지 않는 정렬 조건입니다. filterType = " + filterType);
        }
    }

    public static PageResponse<RoadmapResponse> convertRoadmapPageResponse(final Page<Roadmap> roadmapPages,
                                                                           final CustomPageRequest pageRequest) {
        final int currentPage = pageRequest.getOriginPage();
        final int totalPages = roadmapPages.getTotalPages();
        final List<RoadmapResponse> roadmapResponses = roadmapPages.getContent()
                .stream().map(RoadmapMapper::convertRoadmapResponse)
                .toList();
        return new PageResponse<>(currentPage, totalPages, roadmapResponses);
    }

    public static List<RoadmapCategoryResponse> convertRoadmapCategoryResponses(
            final List<RoadmapCategory> roadmapCategories) {
        return roadmapCategories.stream()
                .map(category -> new RoadmapCategoryResponse(category.getId(), category.getName()))
                .toList();
    }

    public static RoadmapNodeResponse convertNode(final RoadmapNode node) {
        final List<String> images = node.getImages()
                .stream()
                .map(RoadmapNodeImage::getServerFilePath)
                .toList();

        return new RoadmapNodeResponse(node.getTitle(), node.getContent(), images);
    }

    private static RoadmapResponse convertRoadmapResponse(final Roadmap roadmap) {
        final RoadmapCategory category = roadmap.getCategory();
        final RoadmapCategoryResponse categoryResponse = new RoadmapCategoryResponse(category.getId(),
                category.getName());

        final Member creator = roadmap.getCreator();
        final MemberResponse creatorResponse = new MemberResponse(creator.getId(),
                creator.getNickname().getValue());

        return new RoadmapResponse(roadmap.getId(), roadmap.getTitle(), roadmap.getIntroduction(),
                roadmap.getDifficulty().name(), roadmap.getRequiredPeriod(),
                creatorResponse, categoryResponse);
    }

    public static RoadmapResponse convertToRoadmapResponse(final Roadmap roadmap, final RoadmapContent content) {
        final RoadmapCategory category = roadmap.getCategory();
        final Member creator = roadmap.getCreator();

        return new RoadmapResponse(
                roadmap.getId(),
                new RoadmapCategoryResponse(category.getId(), category.getName()),
                roadmap.getTitle(),
                roadmap.getIntroduction(),
                new MemberResponse(creator.getId(), creator.getNickname().getValue()),
                content.getContent(),
                roadmap.getDifficulty().name(),
                roadmap.getRequiredPeriod(),
                convertRoadmapNodeResponse(content.getNodes())
        );
    }

    private static List<RoadmapNodeResponse> convertRoadmapNodeResponse(final RoadmapNodes nodes) {
        return nodes.getRoadmapNodes()
                .stream()
                .map(RoadmapMapper::convertNode)
                .toList();
    }
}
