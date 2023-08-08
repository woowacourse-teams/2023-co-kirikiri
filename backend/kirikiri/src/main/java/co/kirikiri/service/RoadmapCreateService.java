package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.Roadmap;
import co.kirikiri.domain.roadmap.RoadmapCategory;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapDifficulty;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.domain.roadmap.RoadmapReview;
import co.kirikiri.domain.roadmap.RoadmapTag;
import co.kirikiri.domain.roadmap.RoadmapTags;
import co.kirikiri.domain.roadmap.vo.RoadmapTagName;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapCategoryRepository;
import co.kirikiri.persistence.roadmap.RoadmapRepository;
import co.kirikiri.persistence.roadmap.RoadmapReviewRepository;
import co.kirikiri.service.dto.roadmap.RoadmapNodeSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapReviewDto;
import co.kirikiri.service.dto.roadmap.RoadmapSaveDto;
import co.kirikiri.service.dto.roadmap.RoadmapTagSaveDto;
import co.kirikiri.service.dto.roadmap.request.RoadmapReviewSaveRequest;
import co.kirikiri.service.dto.roadmap.request.RoadmapSaveRequest;
import co.kirikiri.service.mapper.RoadmapMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoadmapCreateService {

    private final MemberRepository memberRepository;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapReviewRepository roadmapReviewRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final RoadmapCategoryRepository roadmapCategoryRepository;

    public Long create(final RoadmapSaveRequest request, final String identifier) {
        final Member member = findMemberByIdentifier(identifier);
        final RoadmapCategory roadmapCategory = findRoadmapCategoryById(request.categoryId());
        final RoadmapSaveDto roadmapSaveDto = RoadmapMapper.convertToRoadmapSaveDto(request);
        final Roadmap roadmap = createRoadmap(member, roadmapSaveDto, roadmapCategory);

        return roadmapRepository.save(roadmap).getId();
    }

    private Member findMemberByIdentifier(final String identifier) {
        return memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 회원입니다."));
    }

    private RoadmapCategory findRoadmapCategoryById(final Long categoryId) {
        return roadmapCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 카테고리입니다. categoryId = " + categoryId));
    }

    private Roadmap createRoadmap(final Member member, final RoadmapSaveDto roadmapSaveDto,
                                  final RoadmapCategory roadmapCategory) {
        final RoadmapNodes roadmapNodes = makeRoadmapNodes(roadmapSaveDto.roadmapNodes());
        final RoadmapContent roadmapContent = makeRoadmapContent(roadmapSaveDto, roadmapNodes);
        final RoadmapTags roadmapTags = makeRoadmapTags(roadmapSaveDto.tags());
        final Roadmap roadmap = makeRoadmap(member, roadmapSaveDto, roadmapCategory);
        roadmap.addContent(roadmapContent);
        roadmap.addTags(roadmapTags);
        return roadmap;
    }

    private RoadmapNodes makeRoadmapNodes(final List<RoadmapNodeSaveDto> roadmapNodeSaveDtos) {
        return new RoadmapNodes(
                roadmapNodeSaveDtos.stream()
                        .map(node -> new RoadmapNode(node.title(), node.content()))
                        .toList()
        );
    }

    private RoadmapContent makeRoadmapContent(final RoadmapSaveDto roadmapSaveDto, final RoadmapNodes roadmapNodes) {
        final RoadmapContent roadmapContent = new RoadmapContent(roadmapSaveDto.content());
        roadmapContent.addNodes(roadmapNodes);
        return roadmapContent;
    }

    private RoadmapTags makeRoadmapTags(final List<RoadmapTagSaveDto> roadmapTagSaveDto) {
        return new RoadmapTags(
                roadmapTagSaveDto.stream()
                        .map(tag -> new RoadmapTag(new RoadmapTagName(tag.name())))
                        .toList()
        );
    }

    private Roadmap makeRoadmap(final Member member, final RoadmapSaveDto roadmapSaveDto,
                                final RoadmapCategory roadmapCategory) {
        return new Roadmap(roadmapSaveDto.title(), roadmapSaveDto.introduction(),
                roadmapSaveDto.requiredPeriod(), RoadmapDifficulty.valueOf(roadmapSaveDto.difficulty().name()), member,
                roadmapCategory);
    }

    @Transactional
    public void createReview(final Long roadmapId, final String identifier, final RoadmapReviewSaveRequest request) {
        final Roadmap roadmap = findRoadmapById(roadmapId);
        final GoalRoomMember goalRoomMember = findCompletedGoalRoomMember(roadmapId, identifier);
        final Member member = goalRoomMember.getMember();
        final RoadmapReviewDto roadmapReviewDto = RoadmapMapper.convertRoadmapReviewDto(request, member);
        validateReviewQualification(roadmap, member);
        validateReviewCount(roadmap, member);
        final RoadmapReview roadmapReview = new RoadmapReview(roadmapReviewDto.content(), roadmapReviewDto.rate(),
                roadmapReviewDto.member());
        roadmap.addReview(roadmapReview);
    }

    private Roadmap findRoadmapById(final Long id) {
        return roadmapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다. roadmapId = " + id));
    }

    private GoalRoomMember findCompletedGoalRoomMember(final Long roadmapId, final String identifier) {
        return goalRoomMemberRepository.findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(roadmapId,
                        new Identifier(identifier), GoalRoomStatus.COMPLETED)
                .orElseThrow(() -> new BadRequestException(
                        "로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = " + roadmapId + " memberIdentifier = " + identifier));
    }

    private void validateReviewQualification(final Roadmap roadmap, final Member member) {
        if (roadmap.isCreator(member)) {
            throw new BadRequestException(
                    "로드맵 생성자는 리뷰를 달 수 없습니다. roadmapId = " + roadmap.getId() + " memberId = " + member.getId());
        }
    }

    private void validateReviewCount(final Roadmap roadmap, final Member member) {
        if (roadmapReviewRepository.findByRoadmapAndMember(roadmap, member).isPresent()) {
            throw new BadRequestException(
                    "이미 작성한 리뷰가 존재합니다. roadmapId = " + roadmap.getId() + " memberId = " + member.getId());
        }
    }
}
