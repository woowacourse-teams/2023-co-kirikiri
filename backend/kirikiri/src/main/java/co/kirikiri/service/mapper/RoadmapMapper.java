package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.domain.roadmap.RoadmapTags;
import co.kirikiri.persistence.dto.RoadmapOrderType;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapContentDto;
import co.kirikiri.service.dto.roadmap.RoadmapDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomNumberDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapReviewDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagSaveDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapOrderTypeRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapTagSaveRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapContentResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapNodeResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapReviewResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapTagResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoadmapMapper {

    public static RoadmapSaveDto convertToRoadmapSaveDto(final RoadmapSaveRequest request) {
        final List<RoadmapNodeSaveDto> roadmapNodes = request.roadmapNodes().stream()
                .map(RoadmapMapper::convertToRoadmapNodesSaveDto)
                .toList();
        final List<RoadmapTagSaveDto> roadmapTags = convertToRoadmapTagSaveDtos(request);

        return new RoadmapSaveDto(request.categoryId(), request.title(), request.introduction(), request.content(),
                request.difficulty(), request.requiredPeriod(), roadmapNodes, roadmapTags);
    }

    private static List<RoadmapTagSaveDto> convertToRoadmapTagSaveDtos(final RoadmapSaveRequest request) {
        if (request.roadmapTags() == null) {
            return Collections.emptyList();
        }
        return request.roadmapTags()
                .stream()
                .map(RoadmapMapper::convertToRoadmapTagSaveDto)
                .toList();
    }

    private static RoadmapNodeSaveDto convertToRoadmapNodesSaveDto(final RoadmapNodeSaveRequest request) {
        return new RoadmapNodeSaveDto(request.getTitle(), request.getContent(), request.getImages());
    }

    private static RoadmapTagSaveDto convertToRoadmapTagSaveDto(final RoadmapTagSaveRequest request) {
        return new RoadmapTagSaveDto(request.name());
    }

    public static RoadmapResponse convertToRoadmapResponse(final RoadmapDto roadmapDto, final RoadmapGoalRoomNumberDto roadmapGoalRoomNumberDto) {
        return new RoadmapResponse(
                roadmapDto.roadmapId(),
                new RoadmapCategoryResponse(roadmapDto.category().id(), roadmapDto.category().name()),
                roadmapDto.roadmapTitle(),
                roadmapDto.introduction(),
                new MemberResponse(roadmapDto.creator().id(), roadmapDto.creator().name(), roadmapDto.creator().imageUrl()),
                convertToRoadmapContentResponse(roadmapDto.content()),
                roadmapDto.difficulty(),
                roadmapDto.recommendedRoadmapPeriod(),
                roadmapDto.createdAt(),
                convertRoadmapTagResponses(roadmapDto.tags()),
                roadmapGoalRoomNumberDto.recruitedGoalRoomNumber(),
                roadmapGoalRoomNumberDto.runningGoalRoomNumber(),
                roadmapGoalRoomNumberDto.completedGoalRoomNumber()
        );
    }

    private static RoadmapContentResponse convertToRoadmapContentResponse(final RoadmapContentDto roadmapContentDto) {
        return new RoadmapContentResponse(
                roadmapContentDto.id(),
                roadmapContentDto.content(),
                convertRoadmapNodeResponse(roadmapContentDto.nodes())
        );
    }

    private static List<RoadmapNodeResponse> convertRoadmapNodeResponse(final List<RoadmapNodeDto> roadmapNodeDtos) {
        return roadmapNodeDtos.stream()
                .map(it -> new RoadmapNodeResponse(it.id(), it.title(), it.description(), it.imageUrls()))
                .toList();
    }

    public static RoadmapOrderType convertRoadmapOrderType(final RoadmapOrderTypeRequest filterType) {
        if (filterType == null) {
            return RoadmapOrderType.LATEST;
        }
        return RoadmapOrderType.valueOf(filterType.name());
    }

    private static List<RoadmapTagResponse> convertRoadmapTagResponses(final List<RoadmapTagDto> roadmapTagDtos) {
        return roadmapTagDtos.stream()
                .map(tag -> new RoadmapTagResponse(tag.id(), tag.name()))
                .toList();
    }

    public static RoadmapForListResponses convertRoadmapResponses(final List<Roadmap> roadmaps, final int requestSize) {
        final List<RoadmapForListResponse> responses = roadmaps.stream()
                .map(RoadmapMapper::convertRoadmapResponse)
                .toList();
        final List<RoadmapForListResponse> subResponses = ScrollResponseMapper.getSubResponses(responses, requestSize);
        final boolean hasNext = ScrollResponseMapper.hasNext(responses.size(), requestSize);
        return new RoadmapForListResponses(subResponses, hasNext);
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

    private static List<RoadmapTagResponse> convertRoadmapTagResponses(final RoadmapTags roadmapTags) {
        return roadmapTags.getValues()
                .stream()
                .map(tag -> new RoadmapTagResponse(tag.getId(), tag.getName().getValue()))
                .toList();
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

    public static MemberRoadmapResponses convertMemberRoadmapResponses(final List<Roadmap> roadmaps,
                                                                       final int requestSize) {
        final List<MemberRoadmapResponse> responses = roadmaps.stream()
                .map(RoadmapMapper::convertMemberRoadmapResponse)
                .toList();

        final List<MemberRoadmapResponse> subResponses = ScrollResponseMapper.getSubResponses(responses, requestSize);
        final boolean hasNext = ScrollResponseMapper.hasNext(responses.size(), requestSize);
        return new MemberRoadmapResponses(subResponses, hasNext);
    }

    private static MemberRoadmapResponse convertMemberRoadmapResponse(final Roadmap roadmap) {
        final RoadmapCategory category = roadmap.getCategory();
        return new MemberRoadmapResponse(roadmap.getId(), roadmap.getTitle(),
                roadmap.getDifficulty().name(), roadmap.getCreatedAt(),
                new RoadmapCategoryResponse(category.getId(), category.getName()));
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
