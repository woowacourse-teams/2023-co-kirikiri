package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.dto.RoadmapOrderType;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterType;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class RoadmapMapper {

    public static RoadmapOrderType convertRoadmapOrderType(final RoadmapFilterType filterType) {
        if (filterType == null) {
            return RoadmapOrderType.LATEST;
        }
        try {
            return RoadmapOrderType.valueOf(filterType.name());
        } catch (final IllegalArgumentException e) {
            throw new NotFoundException("존재하지 않는 정렬 조건입니다. filterType = " + filterType);
        }
    }

    public static PageResponse<RoadmapResponse> convertRoadmapPageResponse(final Page<Roadmap> roadmapPages,
                                                                           final Pageable pageable) {
        final int currentPage = pageable.getPageNumber();
        final int totalPages = roadmapPages.getTotalPages();
        final List<RoadmapResponse> roadmapResponses = roadmapPages.getContent()
            .stream().map(RoadmapMapper::convertRoadmapResponse)
            .toList();
        return new PageResponse<>(currentPage, totalPages, roadmapResponses);
    }

    private static RoadmapResponse convertRoadmapResponse(final Roadmap roadmap) {
        final RoadmapCategory category = roadmap.getCategory();
        final RoadmapCategoryResponse categoryResponse = new RoadmapCategoryResponse(category.getId(),
            category.getName());

        final Member creator = roadmap.getCreator();
        final MemberResponse creatorResponse = new MemberResponse(creator.getId(),
            creator.getMemberProfile().getNickname());

        return new RoadmapResponse(roadmap.getId(), roadmap.getTitle(), roadmap.getIntroduction(),
            roadmap.getDifficulty().name(), roadmap.getRequiredPeriod(),
            creatorResponse, categoryResponse);
    }
}
