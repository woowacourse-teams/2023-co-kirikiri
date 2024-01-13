package co.kirikiri.roadmap.service.mapper;

import co.kirikiri.common.dto.FileInformation;
import co.kirikiri.common.mapper.ScrollResponseMapper;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.persistence.dto.RoadmapOrderType;
import co.kirikiri.roadmap.service.dto.MemberDto;
import co.kirikiri.roadmap.service.dto.RoadmapCategoryDto;
import co.kirikiri.roadmap.service.dto.RoadmapContentDto;
import co.kirikiri.roadmap.service.dto.RoadmapDto;
import co.kirikiri.roadmap.service.dto.RoadmapForListDto;
import co.kirikiri.roadmap.service.dto.RoadmapForListScrollDto;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomNumberDto;
import co.kirikiri.roadmap.service.dto.RoadmapNodeDto;
import co.kirikiri.roadmap.service.dto.RoadmapNodeSaveDto;
import co.kirikiri.roadmap.service.dto.RoadmapReviewDto;
import co.kirikiri.roadmap.service.dto.RoadmapReviewReadDto;
import co.kirikiri.roadmap.service.dto.RoadmapSaveDto;
import co.kirikiri.roadmap.service.dto.RoadmapTagDto;
import co.kirikiri.roadmap.service.dto.RoadmapTagSaveDto;
import co.kirikiri.roadmap.service.dto.request.RoadmapCategorySaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapNodeSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapOrderTypeRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapReviewSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapTagSaveRequest;
import co.kirikiri.roadmap.service.dto.response.MemberResponse;
import co.kirikiri.roadmap.service.dto.response.MemberRoadmapResponse;
import co.kirikiri.roadmap.service.dto.response.MemberRoadmapResponses;
import co.kirikiri.roadmap.service.dto.response.RoadmapCategoryResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapContentResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapForListResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapForListResponses;
import co.kirikiri.roadmap.service.dto.response.RoadmapNodeResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapReviewResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapTagResponse;
import co.kirikiri.service.exception.ServerException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoadmapMapper {

    public static RoadmapSaveDto convertToRoadmapSaveDto(final RoadmapSaveRequest request) {
        final List<RoadmapNodeSaveDto> roadmapNodes = request.roadmapNodes()
                .stream()
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
        final List<FileInformation> fileInformations = request.getImages()
                .stream()
                .map(RoadmapMapper::converToRoadmapNodeImageDto)
                .toList();
        return new RoadmapNodeSaveDto(request.getTitle(), request.getContent(), fileInformations);
    }

    private static FileInformation converToRoadmapNodeImageDto(final MultipartFile it) {
        try {
            return new FileInformation(it.getOriginalFilename(), it.getSize(), it.getContentType(),
                    it.getInputStream());
        } catch (final IOException exception) {
            throw new ServerException(exception.getMessage());
        }
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
                                                           final Long memberId) {
        return new RoadmapReviewDto(request.content(), request.rate(), memberId);
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

    public static RoadmapCategory convertToRoadmapCategory(final RoadmapCategorySaveRequest request) {
        return new RoadmapCategory(request.name());
    }
}
