package co.kirikiri.service.mapper;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.persistence.dto.RoadmapOrderType;
import co.kirikiri.service.dto.member.MemberDto;
import co.kirikiri.service.dto.member.response.MemberResponse;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryDto;
import co.kirikiri.service.dto.roadmap.RoadmapContentDto;
import co.kirikiri.service.dto.roadmap.RoadmapDto;
import co.kirikiri.service.dto.roadmap.RoadmapForListDto;
import co.kirikiri.service.dto.roadmap.RoadmapForListScrollDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomNumberDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapReviewDto;
import co.kirikiri.service.dto.roadmap.RoadmapReviewReadDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagSaveDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapNodeSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapOrderTypeRequest;
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
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

    public static RoadmapResponse convertToRoadmapResponse(final RoadmapDto roadmapDto,
                                                           final RoadmapGoalRoomNumberDto roadmapGoalRoomNumberDto) {
        return new RoadmapResponse(
                roadmapDto.roadmapId(),
                new RoadmapCategoryResponse(roadmapDto.category().id(), roadmapDto.category().name()),
                roadmapDto.roadmapTitle(),
                roadmapDto.introduction(),
                new MemberResponse(roadmapDto.creator().id(), roadmapDto.creator().name(),
                        roadmapDto.creator().imageUrl()),
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

    public static RoadmapForListResponses convertRoadmapResponses(
            final RoadmapForListScrollDto roadmapForListScrollDto) {
        final List<RoadmapForListResponse> responses = roadmapForListScrollDto.dtos()
                .stream()
                .map(RoadmapMapper::convertRoadmapResponse)
                .toList();
        return new RoadmapForListResponses(responses, roadmapForListScrollDto.hasNext());
    }

    private static RoadmapForListResponse convertRoadmapResponse(final RoadmapForListDto roadmapForListDto) {
        final RoadmapCategoryDto roadmapCategoryDto = roadmapForListDto.category();
        final RoadmapCategoryResponse categoryResponse = new RoadmapCategoryResponse(roadmapCategoryDto.id(),
                roadmapCategoryDto.name());
        final MemberDto memberDto = roadmapForListDto.creator();
        final MemberResponse creatorResponse = new MemberResponse(memberDto.id(), memberDto.name(),
                memberDto.imageUrl());
        final List<RoadmapTagResponse> roadmapTagResponses = convertRoadmapTagResponses(roadmapForListDto.tags());

        return new RoadmapForListResponse(
                roadmapForListDto.roadmapId(),
                roadmapForListDto.roadmapTitle(),
                roadmapForListDto.introduction(),
                roadmapForListDto.difficulty(),
                roadmapForListDto.recommendedRoadmapPeriod(),
                roadmapForListDto.createdAt(),
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
            final List<RoadmapReviewReadDto> roadmapReviewReadDtos) {
        return roadmapReviewReadDtos.stream()
                .map(RoadmapMapper::convertToRoadmapReviewResponse)
                .toList();
    }

    private static RoadmapReviewResponse convertToRoadmapReviewResponse(
            final RoadmapReviewReadDto roadmapReviewReadDto) {
        final MemberDto memberDto = roadmapReviewReadDto.member();
        return new RoadmapReviewResponse(roadmapReviewReadDto.id(),
                new MemberResponse(memberDto.id(), memberDto.name(), memberDto.imageUrl()),
                roadmapReviewReadDto.createdAt(), roadmapReviewReadDto.content(), roadmapReviewReadDto.rate());
    }
}
