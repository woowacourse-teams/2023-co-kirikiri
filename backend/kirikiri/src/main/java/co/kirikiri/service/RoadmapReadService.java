package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.domain.roadmap.RoadmapTags;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.dto.RoadmapOrderType;
import co.kirikiri.persistence.dto.RoadmapSearchDto;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsOrderType;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.persistence.roadmap.RoadmapReviewRepository;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.goalroom.RoadmapGoalRoomDto;
import co.kirikiri.service.dto.goalroom.RoadmapGoalRoomScrollDto;
import co.kirikiri.service.dto.member.MemberDto;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryDto;
import co.kirikiri.service.dto.roadmap.RoadmapContentDto;
import co.kirikiri.service.dto.roadmap.RoadmapDto;
import co.kirikiri.service.dto.roadmap.RoadmapForListDto;
import co.kirikiri.service.dto.roadmap.RoadmapForListScrollDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomNumberDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsOrderTypeDto;
import co.kirikiri.service.dto.roadmap.RoadmapNodeDto;
import co.kirikiri.service.dto.roadmap.RoadmapReviewReadDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapOrderTypeRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSearchRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapReviewResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import co.kirikiri.service.mapper.RoadmapMapper;
import co.kirikiri.service.mapper.ScrollResponseMapper;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoadmapReadService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final RoadmapReviewRepository roadmapReviewRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    public RoadmapResponse findRoadmap(final Long id) {
        final Roadmap roadmap = findRoadmapById(id);
        final RoadmapContent recentRoadmapContent = findRecentContent(roadmap);
        final List<GoalRoom> goalRooms = goalRoomRepository.findByRoadmap(roadmap);
        final RoadmapGoalRoomNumberDto roadmapGoalRoomNumberDto = GoalRoomMapper.convertRoadmapGoalRoomDto(goalRooms);
        final RoadmapDto roadmapDto = makeRoadmapDto(roadmap, recentRoadmapContent);
        return RoadmapMapper.convertToRoadmapResponse(roadmapDto, roadmapGoalRoomNumberDto);
    }

    private RoadmapDto makeRoadmapDto(final Roadmap roadmap, final RoadmapContent roadmapContent) {
        final RoadmapCategory category = roadmap.getCategory();
        final Member creator = roadmap.getCreator();
        final RoadmapContentDto roadmapContentDto = new RoadmapContentDto(
                roadmapContent.getId(),
                roadmapContent.getContent(),
                makeRoadmapNodeDtos(roadmapContent.getNodes()));
        return new RoadmapDto(roadmap.getId(), new RoadmapCategoryDto(category.getId(), category.getName()),
                roadmap.getTitle(), roadmap.getIntroduction(), makeMemberDto(creator),
                roadmapContentDto, roadmap.getDifficulty().name(), roadmap.getRequiredPeriod(),
                roadmap.getCreatedAt(), makeRoadmapTagDtos(roadmap.getTags()));
    }

    private MemberDto makeMemberDto(final Member creator) {
        final URL url = fileService.generateUrl(creator.getImage().getServerFilePath(), HttpMethod.GET);
        return new MemberDto(creator.getId(), creator.getNickname().getValue(), url.toExternalForm());
    }

    private List<RoadmapNodeDto> makeRoadmapNodeDtos(final RoadmapNodes nodes) {
        return nodes.getValues()
                .stream()
                .map(this::makeRoadmapNodeDto)
                .toList();
    }

    private RoadmapNodeDto makeRoadmapNodeDto(final RoadmapNode roadmapNode) {
        final List<String> imageUrls = roadmapNode.getRoadmapNodeImages()
                .getValues()
                .stream()
                .map(it -> fileService.generateUrl(it.getServerFilePath(), HttpMethod.GET).toExternalForm())
                .toList();
        return new RoadmapNodeDto(roadmapNode.getId(), roadmapNode.getTitle(), roadmapNode.getContent(), imageUrls);
    }

    private List<RoadmapTagDto> makeRoadmapTagDtos(final RoadmapTags roadmapTags) {
        return roadmapTags.getValues()
                .stream()
                .map(it -> new RoadmapTagDto(it.getId(), it.getName().getValue()))
                .toList();
    }

    private Roadmap findRoadmapById(final Long id) {
        return roadmapRepository.findRoadmapById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
    }

    private RoadmapContent findRecentContent(final Roadmap roadmap) {
        return roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(roadmap)
                .orElseThrow(() -> new NotFoundException("로드맵에 컨텐츠가 존재하지 않습니다."));
    }

    public RoadmapForListResponses findRoadmapsByOrderType(final Long categoryId,
                                                           final RoadmapOrderTypeRequest orderTypeRequest,
                                                           final CustomScrollRequest scrollRequest) {
        final RoadmapCategory category = findCategoryById(categoryId);
        final RoadmapOrderType orderType = RoadmapMapper.convertRoadmapOrderType(orderTypeRequest);
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsByCategory(category, orderType,
                scrollRequest.lastId(), scrollRequest.size());
        final RoadmapForListScrollDto roadmapForListScrollDto = makeRoadmapForListScrollDto(roadmaps,
                scrollRequest.size());
        return RoadmapMapper.convertRoadmapResponses(roadmapForListScrollDto);
    }

    private RoadmapCategory findCategoryById(final Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return roadmapCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }

    public RoadmapForListScrollDto makeRoadmapForListScrollDto(final List<Roadmap> roadmaps, final int requestSize) {
        final List<RoadmapForListDto> roadmapForListDtos = roadmaps.stream()
                .map(this::makeRoadmapForListDto)
                .toList();
        final List<RoadmapForListDto> subDtos = ScrollResponseMapper.getSubResponses(roadmapForListDtos, requestSize);
        final boolean hasNext = ScrollResponseMapper.hasNext(roadmapForListDtos.size(), requestSize);
        return new RoadmapForListScrollDto(subDtos, hasNext);
    }

    private RoadmapForListDto makeRoadmapForListDto(final Roadmap roadmap) {
        final RoadmapCategory category = roadmap.getCategory();
        final RoadmapCategoryDto roadmapCategoryDto = new RoadmapCategoryDto(category.getId(),
                category.getName());
        final Member creator = roadmap.getCreator();
        final URL creatorImageUrl = fileService.generateUrl(creator.getImage().getServerFilePath(), HttpMethod.GET);
        final MemberDto memberDto = new MemberDto(creator.getId(), creator.getNickname().getValue(),
                creatorImageUrl.toExternalForm());
        final List<RoadmapTagDto> roadmapTagDtos = makeRoadmapTagDto(roadmap.getTags());

        return new RoadmapForListDto(
                roadmap.getId(),
                roadmap.getTitle(),
                roadmap.getIntroduction(),
                roadmap.getDifficulty().name(),
                roadmap.getRequiredPeriod(),
                roadmap.getCreatedAt(),
                memberDto,
                roadmapCategoryDto,
                roadmapTagDtos
        );
    }

    private List<RoadmapTagDto> makeRoadmapTagDto(final RoadmapTags roadmapTags) {
        return roadmapTags.getValues()
                .stream()
                .map(tag -> new RoadmapTagDto(tag.getId(), tag.getName().getValue()))
                .toList();
    }

    public RoadmapForListResponses search(final RoadmapOrderTypeRequest orderTypeRequest,
                                          final RoadmapSearchRequest searchRequest,
                                          final CustomScrollRequest scrollRequest) {
        final RoadmapOrderType orderType = RoadmapMapper.convertRoadmapOrderType(orderTypeRequest);
        final RoadmapSearchDto roadmapSearchDto = RoadmapSearchDto.create(
                searchRequest.creatorName(), searchRequest.roadmapTitle(), searchRequest.tagName());
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsByCond(roadmapSearchDto, orderType,
                scrollRequest.lastId(), scrollRequest.size());
        final RoadmapForListScrollDto roadmapForListScrollDto = makeRoadmapForListScrollDto(roadmaps,
                scrollRequest.size());
        return RoadmapMapper.convertRoadmapResponses(roadmapForListScrollDto);
    }

    public List<RoadmapCategoryResponse> findAllRoadmapCategories() {
        final List<RoadmapCategory> roadmapCategories = roadmapCategoryRepository.findAll();
        return RoadmapMapper.convertRoadmapCategoryResponses(roadmapCategories);
    }

    public MemberRoadmapResponses findAllMemberRoadmaps(final String identifier,
                                                        final CustomScrollRequest scrollRequest) {
        final Member member = findMemberByIdentifier(identifier);
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsWithCategoryByMemberOrderByLatest(member,
                scrollRequest.lastId(), scrollRequest.size());
        return RoadmapMapper.convertMemberRoadmapResponses(roadmaps, scrollRequest.size());
    }

    private Member findMemberByIdentifier(final String identifier) {
        return memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    public RoadmapGoalRoomResponses findRoadmapGoalRoomsByOrderType(final Long roadmapId,
                                                                    final RoadmapGoalRoomsOrderTypeDto orderTypeDto,
                                                                    final CustomScrollRequest scrollRequest) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        final RoadmapGoalRoomsOrderType orderType = GoalRoomMapper.convertToGoalRoomOrderType(orderTypeDto);
        final List<GoalRoom> goalRoomsWithPendingMembers = goalRoomRepository.findGoalRoomsWithPendingMembersByRoadmapAndCond(
                roadmap, orderType, scrollRequest.lastId(), scrollRequest.size());
        final RoadmapGoalRoomScrollDto roadmapGoalRoomScrollDto = makeGoalRoomDtos(goalRoomsWithPendingMembers,
                scrollRequest.size());
        return GoalRoomMapper.convertToRoadmapGoalRoomResponses(roadmapGoalRoomScrollDto);
    }

    public RoadmapGoalRoomScrollDto makeGoalRoomDtos(final List<GoalRoom> goalRooms,
                                                     final int requestSize) {
        final List<RoadmapGoalRoomDto> roadmapGoalRoomDtos = goalRooms.stream()
                .map(this::makeGoalRoomDto)
                .toList();
        final List<RoadmapGoalRoomDto> subDtos = ScrollResponseMapper.getSubResponses(roadmapGoalRoomDtos, requestSize);
        final boolean hasNext = ScrollResponseMapper.hasNext(roadmapGoalRoomDtos.size(), requestSize);
        return new RoadmapGoalRoomScrollDto(subDtos, hasNext);
    }

    private RoadmapGoalRoomDto makeGoalRoomDto(final GoalRoom goalRoom) {
        final Member goalRoomLeader = goalRoom.findGoalRoomLeader();
        return new RoadmapGoalRoomDto(goalRoom.getId(), goalRoom.getName().getValue(),
                goalRoom.getCurrentMemberCount(), goalRoom.getLimitedMemberCount().getValue(),
                goalRoom.getCreatedAt(), goalRoom.getStartDate(),
                goalRoom.getEndDate(), makeMemberDto(goalRoomLeader));
    }

    public List<RoadmapReviewResponse> findRoadmapReviews(final Long roadmapId,
                                                          final CustomScrollRequest scrollRequest) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        final List<RoadmapReview> roadmapReviews = roadmapReviewRepository.findRoadmapReviewWithMemberByRoadmapOrderByLatest(
                roadmap, scrollRequest.lastId(), scrollRequest.size());
        final List<RoadmapReviewReadDto> roadmapReviewReadDtos = makeRoadmapReviewReadDtos(roadmapReviews);
        return RoadmapMapper.convertToRoadmapReviewResponses(roadmapReviewReadDtos);
    }

    public List<RoadmapReviewReadDto> makeRoadmapReviewReadDtos(final List<RoadmapReview> roadmapReviews) {
        return roadmapReviews.stream()
                .map(this::makeRoadmapReviewReadDto)
                .toList();
    }

    private RoadmapReviewReadDto makeRoadmapReviewReadDto(final RoadmapReview review) {
        final Member member = review.getMember();
        final URL memberImageURl = fileService.generateUrl(member.getImage().getServerFilePath(), HttpMethod.GET);
        return new RoadmapReviewReadDto(review.getId(),
                new MemberDto(member.getId(), member.getNickname().getValue(), memberImageURl.toExternalForm()),
                review.getCreatedAt(), review.getContent(), review.getRate());
    }
}
