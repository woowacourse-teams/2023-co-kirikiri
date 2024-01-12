package co.kirikiri.roadmap.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import co.kirikiri.roadmap.domain.RoadmapReview;
import co.kirikiri.roadmap.domain.RoadmapTags;
import co.kirikiri.roadmap.persistence.RoadmapCategoryRepository;
import co.kirikiri.roadmap.persistence.RoadmapContentRepository;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import co.kirikiri.roadmap.persistence.RoadmapReviewRepository;
import co.kirikiri.roadmap.persistence.dto.RoadmapOrderType;
import co.kirikiri.roadmap.persistence.dto.RoadmapSearchDto;
import co.kirikiri.roadmap.service.dto.RoadmapCategoryDto;
import co.kirikiri.roadmap.service.dto.RoadmapContentDto;
import co.kirikiri.roadmap.service.dto.RoadmapDto;
import co.kirikiri.roadmap.service.dto.RoadmapForListDto;
import co.kirikiri.roadmap.service.dto.RoadmapForListScrollDto;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomNumberDto;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomsOrderTypeDto;
import co.kirikiri.roadmap.service.dto.RoadmapNodeDto;
import co.kirikiri.roadmap.service.dto.RoadmapReviewReadDto;
import co.kirikiri.roadmap.service.dto.RoadmapTagDto;
import co.kirikiri.roadmap.service.dto.request.RoadmapOrderTypeRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapSearchRequest;
import co.kirikiri.roadmap.service.dto.response.MemberRoadmapResponses;
import co.kirikiri.roadmap.service.dto.response.RoadmapCategoryResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapForListResponses;
import co.kirikiri.roadmap.service.dto.response.RoadmapGoalRoomResponses;
import co.kirikiri.roadmap.service.dto.response.RoadmapResponse;
import co.kirikiri.roadmap.service.dto.response.RoadmapReviewResponse;
import co.kirikiri.roadmap.service.mapper.RoadmapMapper;
import co.kirikiri.service.FileService;
import co.kirikiri.service.aop.ExceptionConvert;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.member.MemberDto;
import co.kirikiri.service.exception.NotFoundException;
import co.kirikiri.service.mapper.ScrollResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ExceptionConvert
public class RoadmapReadService {

    private final RoadmapRepository roadmapRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final RoadmapReviewRepository roadmapReviewRepository;
    private final RoadmapGoalRoomService roadmapGoalRoomService;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Cacheable(value = "roadmap", keyGenerator = "cacheKeyGenerator", cacheManager = "redisCacheManager")
    public RoadmapResponse findRoadmap(final Long id) {
        final Roadmap roadmap = findRoadmapById(id);
        final RoadmapContent recentRoadmapContent = findRecentContent(roadmap);
        final RoadmapGoalRoomNumberDto roadmapGoalRoomNumberDto = roadmapGoalRoomService.findRoadmapGoalRoomsByRoadmap(roadmap);
        final RoadmapDto roadmapDto = makeRoadmapDto(roadmap, recentRoadmapContent);
        return RoadmapMapper.convertToRoadmapResponse(roadmapDto, roadmapGoalRoomNumberDto);
    }

    private Roadmap findRoadmapById(final Long id) {
        return roadmapRepository.findRoadmapById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
    }

    private RoadmapContent findRecentContent(final Roadmap roadmap) {
        return roadmapContentRepository.findFirstByRoadmapOrderByCreatedAtDesc(roadmap)
                .orElseThrow(() -> new NotFoundException("로드맵에 컨텐츠가 존재하지 않습니다."));
    }

    private RoadmapDto makeRoadmapDto(final Roadmap roadmap, final RoadmapContent roadmapContent) {
        final RoadmapCategory category = roadmap.getCategory();
        final Member creator = findMemberById(roadmap.getCreatorId());
        final RoadmapContentDto roadmapContentDto = new RoadmapContentDto(
                roadmapContent.getId(),
                roadmapContent.getContent(),
                makeRoadmapNodeDtos(roadmapContent.getNodes()));
        return new RoadmapDto(roadmap.getId(), new RoadmapCategoryDto(category.getId(), category.getName()),
                roadmap.getTitle(), roadmap.getIntroduction(), makeMemberDto(creator),
                roadmapContentDto, roadmap.getDifficulty().name(), roadmap.getRequiredPeriod(),
                roadmap.getCreatedAt(), makeRoadmapTagDtos(roadmap.getTags()));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findWithMemberProfileAndImageById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
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

    @Cacheable(value = "roadmapList", keyGenerator = "cacheKeyGenerator", cacheManager = "redisCacheManager")
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

    private RoadmapForListScrollDto makeRoadmapForListScrollDto(final List<Roadmap> roadmaps, final int requestSize) {
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
        final Member creator = findMemberById(roadmap.getCreatorId());
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

    @Cacheable(value = "categoryList", keyGenerator = "cacheKeyGenerator", cacheManager = "redisCacheManager")
    public List<RoadmapCategoryResponse> findAllRoadmapCategories() {
        final List<RoadmapCategory> roadmapCategories = roadmapCategoryRepository.findAll();
        return RoadmapMapper.convertRoadmapCategoryResponses(roadmapCategories);
    }

    public MemberRoadmapResponses findAllMemberRoadmaps(final String identifier,
                                                        final CustomScrollRequest scrollRequest) {
        final Member member = findMemberByIdentifier(identifier);
        final List<Roadmap> roadmaps = roadmapRepository.findRoadmapsWithCategoryByMemberIdOrderByLatest(member,
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
        return roadmapGoalRoomService.makeRoadmapGoalRoomResponsesByOrderType(roadmap, orderTypeDto, scrollRequest);
    }

    public List<RoadmapReviewResponse> findRoadmapReviews(final Long roadmapId,
                                                          final CustomScrollRequest scrollRequest) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        final List<RoadmapReview> roadmapReviews = roadmapReviewRepository.findRoadmapReviewByRoadmapOrderByLatest(
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
        final Member member = findMemberById(review.getMemberId());
        final URL memberImageURl = fileService.generateUrl(member.getImage().getServerFilePath(), HttpMethod.GET);
        return new RoadmapReviewReadDto(review.getId(),
                new MemberDto(member.getId(), member.getNickname().getValue(), memberImageURl.toExternalForm()),
                review.getCreatedAt(), review.getContent(), review.getRate());
    }
}
