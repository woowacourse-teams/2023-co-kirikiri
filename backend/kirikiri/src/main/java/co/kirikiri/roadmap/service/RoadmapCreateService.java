package co.kirikiri.roadmap.service;

import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapCategory;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.domain.RoadmapDifficulty;
import co.kirikiri.roadmap.domain.RoadmapNode;
import co.kirikiri.roadmap.domain.RoadmapNodes;
import co.kirikiri.roadmap.domain.RoadmapReview;
import co.kirikiri.roadmap.domain.RoadmapTag;
import co.kirikiri.roadmap.domain.RoadmapTags;
import co.kirikiri.roadmap.domain.vo.RoadmapTagName;
import co.kirikiri.roadmap.persistence.RoadmapCategoryRepository;
import co.kirikiri.roadmap.persistence.RoadmapContentRepository;
import co.kirikiri.roadmap.persistence.RoadmapRepository;
import co.kirikiri.roadmap.persistence.RoadmapReviewRepository;
import co.kirikiri.roadmap.service.dto.RoadmapNodeSaveDto;
import co.kirikiri.roadmap.service.dto.RoadmapReviewDto;
import co.kirikiri.roadmap.service.dto.RoadmapSaveDto;
import co.kirikiri.roadmap.service.dto.RoadmapTagSaveDto;
import co.kirikiri.roadmap.service.dto.request.RoadmapCategorySaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapReviewSaveRequest;
import co.kirikiri.roadmap.service.dto.request.RoadmapSaveRequest;
import co.kirikiri.roadmap.service.event.RoadmapCreateEvent;
import co.kirikiri.roadmap.service.mapper.RoadmapMapper;
import co.kirikiri.service.aop.ExceptionConvert;
import co.kirikiri.service.exception.AuthenticationException;
import co.kirikiri.service.exception.BadRequestException;
import co.kirikiri.service.exception.ConflictException;
import co.kirikiri.service.exception.ForbiddenException;
import co.kirikiri.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class RoadmapCreateService {

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final RoadmapReviewRepository roadmapReviewRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;
    private final RoadmapGoalRoomService roadmapGoalRoomService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Long create(final RoadmapSaveRequest request, final String identifier) {
        final Member member = findMemberByIdentifier(identifier);
        final RoadmapCategory roadmapCategory = findRoadmapCategoryById(request.categoryId());
        final RoadmapSaveDto roadmapSaveDto = RoadmapMapper.convertToRoadmapSaveDto(request);

        final Roadmap roadmap = makeRoadmap(member.getId(), roadmapSaveDto, roadmapCategory);
        final Roadmap savedRoadmap = roadmapRepository.save(roadmap);

        final RoadmapContent roadmapContent = makeRoadmapContent(roadmapSaveDto, savedRoadmap.getId());
        roadmapContentRepository.save(roadmapContent);

        applicationEventPublisher.publishEvent(new RoadmapCreateEvent(savedRoadmap.getId(), roadmapSaveDto));

        return savedRoadmap.getId();
    }

    private Member findMemberByIdentifier(final String identifier) {
        return memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 회원입니다."));
    }

    private RoadmapCategory findRoadmapCategoryById(final Long categoryId) {
        return roadmapCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }

    private Roadmap makeRoadmap(final Long memberId, final RoadmapSaveDto roadmapSaveDto,
                                final RoadmapCategory roadmapCategory) {
        final RoadmapTags roadmapTags = makeRoadmapTags(roadmapSaveDto.tags());
        return new Roadmap(roadmapSaveDto.title(), roadmapSaveDto.introduction(),
                roadmapSaveDto.requiredPeriod(), RoadmapDifficulty.valueOf(roadmapSaveDto.difficulty().name()), memberId,
                roadmapCategory, roadmapTags);
    }

    private RoadmapTags makeRoadmapTags(final List<RoadmapTagSaveDto> roadmapTagSaveDto) {
        return new RoadmapTags(
                roadmapTagSaveDto.stream()
                        .map(tag -> new RoadmapTag(new RoadmapTagName(tag.name())))
                        .toList()
        );
    }

    private RoadmapContent makeRoadmapContent(final RoadmapSaveDto roadmapSaveDto, final Long roadmapId) {
        final RoadmapNodes nodes = makeRoadmapNodes(roadmapSaveDto.roadmapNodes());
        return new RoadmapContent(roadmapSaveDto.content(), roadmapId, nodes);
    }

    private RoadmapNodes makeRoadmapNodes(final List<RoadmapNodeSaveDto> roadmapNodeSaveDtos) {
        return new RoadmapNodes(
                roadmapNodeSaveDtos.stream()
                        .map(node -> new RoadmapNode(node.title(), node.content()))
                        .toList()
        );
    }

    public void createReview(final Long roadmapId, final String identifier, final RoadmapReviewSaveRequest request) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        final Long memberId = roadmapGoalRoomService.findCompletedGoalRoomMember(roadmapId, identifier)
                .getId();

        validateReviewQualification(roadmap, memberId);
        validateReviewCount(roadmapId, memberId);
        final RoadmapReviewDto roadmapReviewDto = RoadmapMapper.convertRoadmapReviewDto(request, memberId);
        final RoadmapReview roadmapReview = new RoadmapReview(roadmapReviewDto.content(), roadmapReviewDto.rate(), memberId, roadmapId);
        roadmapReviewRepository.save(roadmapReview);
    }

    private Roadmap findRoadmapById(final Long id) {
        return roadmapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
    }

    private void validateReviewQualification(final Roadmap roadmap, final Long memberId) {
        if (roadmap.isCreator(memberId)) {
            throw new BadRequestException(
                    "로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = " + roadmap.getId() + " memberId = " + memberId);
        }
    }

    private void validateReviewCount(final Long roadmapId, final Long memberId) {
        if (roadmapReviewRepository.findByRoadmapIdAndMemberId(roadmapId, memberId).isPresent()) {
            throw new BadRequestException(
                    "이미 작성한 리뷰가 존재합니다. roadmapId = " + roadmapId + " memberId = " + memberId);
        }
    }

    @CacheEvict(value = {"roadmapList", "roadmap"}, allEntries = true)
    public void deleteRoadmap(final String identifier, final Long roadmapId) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        validateRoadmapCreator(roadmapId, identifier);
        if (!roadmapGoalRoomService.hasGoalRooms(roadmapId)) {
            roadmapRepository.delete(roadmap);
            roadmapContentRepository.deleteAllByRoadmapId(roadmapId);
            roadmapReviewRepository.deleteAllByRoadmapId(roadmapId);
            return;
        }
        roadmap.delete();
    }

    private void validateRoadmapCreator(final Long roadmapId, final String identifier) {
        if (roadmapRepository.findByIdAndMemberIdentifier(roadmapId, identifier).isEmpty()) {
            throw new ForbiddenException("해당 로드맵을 생성한 사용자가 아닙니다.");
        }
    }

    @CacheEvict(value = "categoryList", allEntries = true)
    public void createRoadmapCategory(final RoadmapCategorySaveRequest roadmapCategorySaveRequest) {
        final RoadmapCategory roadmapCategory = RoadmapMapper.convertToRoadmapCategory(roadmapCategorySaveRequest);
        roadmapCategoryRepository.findByName(roadmapCategory.getName())
                .ifPresent(it -> {
                    throw new ConflictException("이미 존재하는 이름의 카테고리입니다.");
                });
        roadmapCategoryRepository.save(roadmapCategory);
    }
}
