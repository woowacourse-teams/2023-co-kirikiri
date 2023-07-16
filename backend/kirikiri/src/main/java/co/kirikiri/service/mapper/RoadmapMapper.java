package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.dto.RoadmapFilterType;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.member.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.RoadmapFilterTypeDto;
import co.kirikiri.service.dto.roadmap.RoadmapResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoadmapMapper {

    public static RoadmapFilterType convertRoadmapOrderType(final RoadmapFilterTypeDto filterType) {
        if (filterType == null) {
            return RoadmapFilterType.LATEST;
        }
        return RoadmapFilterType.valueOf(filterType.name());
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

    public static List<RoadmapCategoryResponse> convertRoadmapCategoryResponses(
        final List<RoadmapCategory> roadmapCategories) {
        return roadmapCategories.stream()
            .map(category -> new RoadmapCategoryResponse(category.getId(), category.getName()))
            .toList();
    }
}