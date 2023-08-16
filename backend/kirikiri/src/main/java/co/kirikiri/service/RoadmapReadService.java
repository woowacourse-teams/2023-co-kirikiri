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
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsFilterType;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.persistence.roadmap.RoadmapReviewRepository;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.member.MemberDto;
import co.kirikiri.service.dto.roadmap.RoadmapCategoryDto;
import co.kirikiri.service.dto.roadmap.RoadmapContentDto;
import co.kirikiri.service.dto.roadmap.RoadmapDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomNumberDto;
import co.kirikiri.service.dto.roadmap.RoadmapGoalRoomsFilterTypeDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapOrderTypeRequest;
import co.kirikiri.service.dto.roadmap.RoadmapNodeDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapSearchRequest;
import co.kirikiri.service.dto.roadmap.response.MemberRoadmapResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapCategoryResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapForListResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.dto.roadmap.response.RoadmapResponse;
import co.kirikiri.service.dto.roadmap.response.RoadmapReviewResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import co.kirikiri.service.mapper.RoadmapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URL;
import java.util.List;

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
        return new MemberDto(creator.getId(), creator.getNickname().getValue(), url.getPath());
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

    public RoadmapForListResponses findRoadmapsByFilterType(final Long categoryId,
                                                            final RoadmapOrderTypeRequest filterType,
                                                            final CustomScrollRequest scrollRequest) {
        final RoadmapCategory category = findCategoryById(categoryId);
        final RoadmapOrderType orderType = RoadmapMapper.convertRoadmapOrderType(filterType);
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsByCategory(category, orderType,
                scrollRequest.lastId(), scrollRequest.size());
        return RoadmapMapper.convertRoadmapResponses(roadmaps, scrollRequest.size());
    }

    private RoadmapCategory findCategoryById(final Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return roadmapCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }

    public RoadmapForListResponses search(final RoadmapOrderTypeRequest filterTypeRequest,
                                          final RoadmapSearchRequest searchRequest,
                                          final CustomScrollRequest scrollRequest) {
        final RoadmapOrderType orderType = RoadmapMapper.convertRoadmapOrderType(filterTypeRequest);
        final RoadmapSearchDto roadmapSearchDto = RoadmapSearchDto.create(
                searchRequest.creatorId(), searchRequest.roadmapTitle(), searchRequest.tagName());
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsByCond(roadmapSearchDto, orderType,
                scrollRequest.lastId(), scrollRequest.size());
        return RoadmapMapper.convertRoadmapResponses(roadmaps, scrollRequest.size());
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

    public RoadmapGoalRoomResponses findRoadmapGoalRoomsByFilterType(final Long roadmapId,
                                                                     final RoadmapGoalRoomsFilterTypeDto filterTypeDto,
                                                                     final CustomScrollRequest scrollRequest) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        final RoadmapGoalRoomsFilterType filterType = GoalRoomMapper.convertToGoalRoomFilterType(filterTypeDto);
        final List<GoalRoom> goalRoomsWithPendingMembers = goalRoomRepository.findGoalRoomsWithPendingMembersByRoadmapAndCond(
                roadmap, filterType, scrollRequest.lastId(), scrollRequest.size());
        return GoalRoomMapper.convertToRoadmapGoalRoomResponses(goalRoomsWithPendingMembers, scrollRequest.size());
    }

    public List<RoadmapReviewResponse> findRoadmapReviews(final Long roadmapId,
                                                          final CustomScrollRequest scrollRequest) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        final List<RoadmapReview> roadmapReviews = roadmapReviewRepository.findRoadmapReviewWithMemberByRoadmapOrderByLatest(
                roadmap, scrollRequest.lastId(), scrollRequest.size());
        return RoadmapMapper.convertToRoadmapReviewResponses(roadmapReviews);
    }
}
