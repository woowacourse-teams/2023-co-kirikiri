package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodeImage;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.domain.roadmap.RoadmapTags;
import co.kirikiri.persistence.dto.RoadmapFilterType;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapReviewDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagSaveDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapFilterTypeRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapReviewResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapTagResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoadmapMapper {

    public static RoadmapSaveDto convertToRoadmapSaveDto(final RoadmapSaveRequest request) {
        final List<RoadmapNodeSaveDto> roadmapNodes = request.roadmapNodes().stream()
                .map(RoadmapMapper::convertToRoadmapNodesSaveDto)
                .toList();
        final List<RoadmapTagSaveDto> roadmapTags = request.roadmapTags().stream()
                .map(RoadmapMapper::convertToRoadmapTagSaveDto)
                .toList();

        return new RoadmapSaveDto(request.categoryId(), request.title(), request.introduction(), request.content(),
                request.difficulty(), request.requiredPeriod(), roadmapNodes, roadmapTags);
    }

    private static RoadmapNodeSaveDto convertToRoadmapNodesSaveDto(final RoadmapNodeSaveRequest request) {
        return new RoadmapNodeSaveDto(request.title(), request.content());
    }

    private static RoadmapTagSaveDto convertToRoadmapTagSaveDto(final RoadmapTagSaveRequest request) {
        return new RoadmapTagSaveDto(request.name());
    }

    public static RoadmapResponse convertToRoadmapResponse(final Roadmap roadmap, final RoadmapContent content) {
        final RoadmapCategory category = roadmap.getCategory();
        final Member creator = roadmap.getCreator();
        final RoadmapContentResponse roadmapContentResponse = new RoadmapContentResponse(
                content.getId(),
                content.getContent(),
                convertRoadmapNodeResponse(content.getNodes()));
        final List<RoadmapTagResponse> roadmapTagResponses = convertRoadmapTagResponses(roadmap.getTags());

        return new RoadmapResponse(
                roadmap.getId(),
                new RoadmapCategoryResponse(category.getId(), category.getName()),
                roadmap.getTitle(),
                roadmap.getIntroduction(),
                new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                        creator.getImage().getServerFilePath()),
                roadmapContentResponse,
                roadmap.getDifficulty().name(),
                roadmap.getRequiredPeriod(),
                roadmap.getCreatedAt(),
                roadmapTagResponses
        );
    }

    private static List<RoadmapNodeResponse> convertRoadmapNodeResponse(final RoadmapNodes nodes) {
        return nodes.getValues()
                .stream()
                .map(RoadmapMapper::convertNode)
                .toList();
    }

    private static RoadmapNodeResponse convertNode(final RoadmapNode node) {
        final List<String> images = node.getRoadmapNodeImages().getValues()
                .stream()
                .map(RoadmapNodeImage::getServerFilePath)
                .toList();

        return new RoadmapNodeResponse(node.getId(), node.getTitle(), node.getContent(), images);
    }

    public static RoadmapFilterType convertRoadmapOrderType(final RoadmapFilterTypeRequest filterType) {
        if (filterType == null) {
            return RoadmapFilterType.LATEST;
        }
        return RoadmapFilterType.valueOf(filterType.name());
    }

    private static List<RoadmapTagResponse> convertRoadmapTagResponses(final RoadmapTags roadmapTags) {
        return roadmapTags.getValues()
                .stream()
                .map(tag -> new RoadmapTagResponse(tag.getId(), tag.getName().getValue()))
                .toList();
    }

    public static List<RoadmapForListResponse> convertRoadmapResponses(final List<Roadmap> roadmaps) {
        return roadmaps.stream()
                .map(RoadmapMapper::convertRoadmapResponse)
                .toList();
    }

    private static RoadmapForListResponse convertRoadmapResponse(final Roadmap roadmap) {
        final RoadmapCategory category = roadmap.getCategory();
        final RoadmapCategoryResponse categoryResponse = new RoadmapCategoryResponse(category.getId(),
                category.getName());
        final Member creator = roadmap.getCreator();
        final MemberResponse creatorResponse = new MemberResponse(creator.getId(), creator.getNickname().getValue(),
                creator.getImage().getServerFilePath());
        final List<RoadmapTagResponse> roadmapTagResponses = convertRoadmapTagResponses(roadmap.getTags());

        return new RoadmapForListResponse(
                roadmap.getId(),
                roadmap.getTitle(),
                roadmap.getIntroduction(),
                roadmap.getDifficulty().name(),
                roadmap.getRequiredPeriod(),
                roadmap.getCreatedAt(),
                creatorResponse,
                categoryResponse,
                roadmapTagResponses
        );
    }

    public static List<RoadmapCategoryResponse> convertRoadmapCategoryResponses(
            final List<RoadmapCategory> roadmapCategories) {
        return roadmapCategories.stream()
                .map(category -> new RoadmapCategoryResponse(category.getId(), category.getName()))
                .toList();
    }

    public static RoadmapReviewDto convertRoadmapReviewDto(final RoadmapReviewSaveRequest request,
                                                           final Member member) {
        return new RoadmapReviewDto(request.content(), request.rate(), member);
    }

    public static List<MemberRoadmapResponse> convertMemberRoadmapResponses(final List<Roadmap> roadmaps) {
        return roadmaps.stream()
                .map(roadmap -> {
                    final RoadmapCategory category = roadmap.getCategory();
                    return new MemberRoadmapResponse(roadmap.getId(), roadmap.getTitle(),
                            roadmap.getDifficulty().name(), roadmap.getCreatedAt(),
                            new RoadmapCategoryResponse(category.getId(), category.getName()));
                })
                .toList();
    }

    public static List<RoadmapReviewResponse> convertToRoadmapReviewResponses(
            final List<RoadmapReview> roadmapReviews) {
        return roadmapReviews.stream()
                .map(RoadmapMapper::convertToRoadmapReviewResponse)
                .toList();
    }

    private static RoadmapReviewResponse convertToRoadmapReviewResponse(final RoadmapReview review) {
        final Member member = review.getMember();
        return new RoadmapReviewResponse(review.getId(),
                new MemberResponse(member.getId(), member.getNickname().getValue(),
                        member.getImage().getServerFilePath()),
                review.getCreatedAt(), review.getContent(), review.getRate());
    }
}
