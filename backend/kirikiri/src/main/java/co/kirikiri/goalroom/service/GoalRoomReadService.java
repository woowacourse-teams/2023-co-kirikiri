package co.kirikiri.goalroom.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.ForbiddenException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.service.FileService;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.domain.roadmap.RoadmapNodes;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomMember;
import co.kirikiri.goalroom.domain.GoalRoomPendingMember;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNodes;
import co.kirikiri.goalroom.domain.GoalRoomRole;
import co.kirikiri.goalroom.domain.GoalRoomStatus;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.goalroom.persistence.dto.GoalRoomMemberSortType;
import co.kirikiri.goalroom.service.dto.GoalRoomMemberDto;
import co.kirikiri.goalroom.service.dto.GoalRoomMemberSortTypeDto;
import co.kirikiri.goalroom.service.dto.GoalRoomRoadmapNodeDetailDto;
import co.kirikiri.goalroom.service.dto.MemberDto;
import co.kirikiri.goalroom.service.dto.MemberGoalRoomForListDto;
import co.kirikiri.goalroom.service.dto.request.GoalRoomStatusTypeRequest;
import co.kirikiri.goalroom.service.dto.response.DashBoardCheckFeedResponse;
import co.kirikiri.goalroom.service.dto.response.DashBoardToDoResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomCertifiedResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomMemberResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomResponse;
import co.kirikiri.goalroom.service.dto.response.GoalRoomRoadmapNodeDetailResponse;
import co.kirikiri.goalroom.service.dto.response.MemberGoalRoomForListResponse;
import co.kirikiri.goalroom.service.dto.response.MemberGoalRoomResponse;
import co.kirikiri.goalroom.service.mapper.GoalRoomMapper;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.persistence.roadmap.RoadmapNodeRepository;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ExceptionConvert
public class GoalRoomReadService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final DashBoardToDoService dashBoardToDoService;
    private final DashBoardCheckFeedService dashBoardCheckFeedService;
    private final RoadmapContentRepository roadmapContentRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    public GoalRoomCertifiedResponse findGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithRoadmapContentById(goalRoomId);
        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoom.getRoadmapContentId());
        final List<RoadmapNode> roadmapNodes = roadmapNodeRepository.findAllByRoadmapContent(roadmapContent);

        final boolean isJoined = isMemberGoalRoomJoin(new Identifier(identifier), goalRoom);
        return GoalRoomMapper.convertGoalRoomCertifiedResponse(goalRoom, getCurrentMemberCount(goalRoom),
                new RoadmapNodes(roadmapNodes), isJoined);
    }

    private GoalRoom findGoalRoomWithRoadmapContentById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    private RoadmapContent findRoadmapContentById(final Long roadmapContentId) {
        return roadmapContentRepository.findById(roadmapContentId)
                .orElseThrow(() -> new NotFoundException(
                        "존재하지 않는 로드맵 컨텐츠 아이디입니다. roadmapContentId = " + roadmapContentId));
    }

    private boolean isMemberGoalRoomJoin(final Identifier identifier, final GoalRoom goalRoom) {
        final Member member = findMemberByIdentifier(identifier);
        if (goalRoom.isRecruiting()) {
            return goalRoomPendingMemberRepository.findByGoalRoomAndMemberId(goalRoom, member.getId()).isPresent();
        }
        return goalRoomMemberRepository.findByGoalRoomAndMemberId(goalRoom, member.getId()).isPresent();
    }

    private int getCurrentMemberCount(final GoalRoom goalRoom) {
        if (goalRoom.isRecruiting()) {
            return goalRoomPendingMemberRepository.findByGoalRoom(goalRoom)
                    .size();
        }
        return goalRoomMemberRepository.findByGoalRoom(goalRoom)
                .size();
    }

    public GoalRoomResponse findGoalRoom(final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithRoadmapContentById(goalRoomId);
        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoom.getRoadmapContentId());
        final List<RoadmapNode> roadmapNodes = roadmapNodeRepository.findAllByRoadmapContent(roadmapContent);
        return GoalRoomMapper.convertGoalRoomResponse(goalRoom, getCurrentMemberCount(goalRoom),
                new RoadmapNodes(roadmapNodes));
    }

    public List<GoalRoomMemberResponse> findGoalRoomMembers(final Long goalRoomId,
                                                            final GoalRoomMemberSortTypeDto sortType) {
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        final GoalRoomMemberSortType goalRoomMemberSortType = GoalRoomMapper.convertGoalRoomMemberSortType(sortType);
        if (goalRoom.isRecruiting()) {
            final List<GoalRoomPendingMember> goalRoomPendingMembers = goalRoomPendingMemberRepository.findByGoalRoomIdOrderedBySortType(
                    goalRoomId, goalRoomMemberSortType);
            final List<GoalRoomMemberDto> goalRoomMemberDtos = makeGoalRoomMemberDtosWithAccomplishmentRateZero(
                    goalRoomPendingMembers);
            return GoalRoomMapper.convertToGoalRoomMemberResponses(goalRoomMemberDtos);
        }
        final List<GoalRoomMember> goalRoomMembers = goalRoomMemberRepository.findByGoalRoomIdOrderedBySortType(
                goalRoomId, goalRoomMemberSortType);
        final List<GoalRoomMemberDto> goalRoomMemberDtos = makeGoalRoomMemberDtos(goalRoomMembers);
        return GoalRoomMapper.convertToGoalRoomMemberResponses(goalRoomMemberDtos);
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findById(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private List<GoalRoomMemberDto> makeGoalRoomMemberDtosWithAccomplishmentRateZero(
            final List<GoalRoomPendingMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMemberDtoWithAccomplishmentRateZero)
                .toList();
    }

    private GoalRoomMemberDto makeGoalRoomMemberDtoWithAccomplishmentRateZero(
            final GoalRoomPendingMember goalRoomPendingMember) {
        final Long memberId = goalRoomPendingMember.getMemberId();
        final Member member = findMemberWithImageById(memberId);
        final URL memberImageUrl = fileService.generateUrl(member.getImage().getServerFilePath(), HttpMethod.GET);
        return new GoalRoomMemberDto(member.getId(), member.getNickname().getValue(),
                memberImageUrl.toExternalForm(), 0D);
    }

    private Member findMemberWithImageById(final Long memberId) {
        return memberRepository.findWithMemberImageById(memberId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸 멤버입니다. memberId = " + memberId));
    }

    private List<GoalRoomMemberDto> makeGoalRoomMemberDtos(
            final List<GoalRoomMember> goalRoomPendingMembers) {
        return goalRoomPendingMembers.stream()
                .map(this::makeGoalRoomMemberDto)
                .toList();
    }

    private GoalRoomMemberDto makeGoalRoomMemberDto(final GoalRoomMember goalRoomMember) {
        final Long memberId = goalRoomMember.getMemberId();
        final Member member = findMemberWithImageById(memberId);
        final URL memberImageUrl = fileService.generateUrl(member.getImage().getServerFilePath(), HttpMethod.GET);
        return new GoalRoomMemberDto(member.getId(), member.getNickname().getValue(),
                memberImageUrl.toExternalForm(), goalRoomMember.getAccomplishmentRate());
    }

    public MemberGoalRoomResponse findMemberGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithNodesById(goalRoomId);
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        validateMemberInGoalRoom(goalRoom, member.getId());

        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoom.getRoadmapContentId());
        final List<RoadmapNode> roadmapNodes = roadmapNodeRepository.findAllByRoadmapContent(roadmapContent);
        final List<DashBoardCheckFeedResponse> checkFeeds = dashBoardCheckFeedService.findCheckFeedsByNodeAndGoalRoomStatus(
                goalRoom);
        final List<DashBoardToDoResponse> checkedTodos = dashBoardToDoService.findMemberCheckedGoalRoomToDoIds(
                goalRoom, member.getId());
        return GoalRoomMapper.convertToMemberGoalRoomResponse(goalRoom, findGoalRoomLeaderId(goalRoom),
                getCurrentMemberCount(goalRoom), new RoadmapNodes(roadmapNodes), checkFeeds, checkedTodos);
    }

    private GoalRoom findGoalRoomWithNodesById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithNodes(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    private Member findMemberByIdentifier(final Identifier identifier) {
        return memberRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private void validateMemberInGoalRoom(final GoalRoom goalRoom, final Long memberId) {
        if (goalRoom.isRecruiting()) {
            goalRoomPendingMemberRepository.findByGoalRoomAndMemberId(goalRoom, memberId)
                    .orElseThrow(() -> new ForbiddenException("해당 골룸에 참여하지 않은 사용자입니다."));
            return;
        }
        goalRoomMemberRepository.findByGoalRoomAndMemberId(goalRoom, memberId)
                .orElseThrow(() -> new ForbiddenException("해당 골룸에 참여하지 않은 사용자입니다."));
    }

    private Long findGoalRoomLeaderId(final GoalRoom goalRoom) {
        if (goalRoom.isRecruiting()) {
            return goalRoomPendingMemberRepository.findLeaderByGoalRoomAndRole(goalRoom, GoalRoomRole.LEADER)
                    .orElseThrow(() -> new NotFoundException("골룸의 리더가 존재하지 않습니다."))
                    .getMemberId();
        }
        return goalRoomMemberRepository.findLeaderByGoalRoomAndRole(goalRoom, GoalRoomRole.LEADER)
                .orElseThrow(() -> new NotFoundException("골룸의 리더가 존재하지 않습니다."))
                .getMemberId();
    }

    public List<MemberGoalRoomForListResponse> findMemberGoalRooms(final String identifier) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        final List<GoalRoom> memberGoalRooms = goalRoomRepository.findByMemberId(member.getId());
        final List<MemberGoalRoomForListDto> memberGoalRoomForListDtos = makeMemberGoalRoomForListDto(
                memberGoalRooms);
        return GoalRoomMapper.convertToMemberGoalRoomForListResponses(memberGoalRoomForListDtos);
    }

    private List<MemberGoalRoomForListDto> makeMemberGoalRoomForListDto(final List<GoalRoom> memberGoalRooms) {
        return memberGoalRooms.stream()
                .map(this::makeMemberGoalRoomForListDto)
                .toList();
    }

    private MemberGoalRoomForListDto makeMemberGoalRoomForListDto(final GoalRoom goalRoom) {
        final Long goalRoomLeaderId = findGoalRoomLeaderId(goalRoom);
        final Member leader = findMemberWithProfileAndImageById(goalRoomLeaderId);
        final URL leaderImageUrl = fileService.generateUrl(leader.getImage().getServerFilePath(), HttpMethod.GET);
        return new MemberGoalRoomForListDto(goalRoom.getId(), goalRoom.getName().getValue(),
                goalRoom.getStatus().name(), getCurrentMemberCount(goalRoom),
                goalRoom.getLimitedMemberCount().getValue(),
                goalRoom.getCreatedAt(), goalRoom.getStartDate(), goalRoom.getEndDate(),
                new MemberDto(leader.getId(), leader.getNickname().getValue(), leaderImageUrl.toExternalForm()));
    }

    private Member findMemberWithProfileAndImageById(final Long goalRoomLeaderId) {
        return memberRepository.findWithMemberProfileAndImageById(goalRoomLeaderId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. memberId = " + goalRoomLeaderId));
    }

    public List<MemberGoalRoomForListResponse> findMemberGoalRoomsByStatusType(final String identifier,
                                                                               final GoalRoomStatusTypeRequest goalRoomStatusTypeRequest) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        final GoalRoomStatus goalRoomStatus = GoalRoomMapper.convertToGoalRoomStatus(goalRoomStatusTypeRequest);
        final List<GoalRoom> memberGoalRooms = goalRoomRepository.findByMemberAndStatus(member.getId(),
                goalRoomStatus.name());
        final List<MemberGoalRoomForListDto> memberGoalRoomForListDtos = makeMemberGoalRoomForListDto(
                memberGoalRooms);
        return GoalRoomMapper.convertToMemberGoalRoomForListResponses(memberGoalRoomForListDtos);
    }

    public List<GoalRoomRoadmapNodeDetailResponse> findAllGoalRoomNodes(final Long goalRoomId,
                                                                        final String identifier) {
        final GoalRoom goalRoom = findGoalRoomWithNodesByGoalRoomId(goalRoomId);
        final GoalRoomRoadmapNodes goalRoomNodes = goalRoom.getGoalRoomRoadmapNodes();
        validateGoalRoomMember(goalRoomId, identifier);
        final List<GoalRoomRoadmapNodeDetailDto> goalRoomRoadmapNodeDetailDtos = makeGoalRoomNodeDetailDtos(
                goalRoomNodes);

        return GoalRoomMapper.convertGoalRoomNodeDetailResponses(goalRoomRoadmapNodeDetailDtos);
    }

    private GoalRoom findGoalRoomWithNodesByGoalRoomId(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithNodes(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private void validateGoalRoomMember(final Long goalRoomId, final String identifier) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        if (goalRoomMemberRepository.findByGoalRoomIdAndMemberId(goalRoomId, member.getId()).isEmpty()) {
            throw new ForbiddenException(
                    "골룸에 참여하지 않은 사용자입니다. goalRoomId = " + goalRoomId + " memberIdentifier = " + identifier);
        }
    }

    private List<GoalRoomRoadmapNodeDetailDto> makeGoalRoomNodeDetailDtos(
            final GoalRoomRoadmapNodes nodes) {
        return nodes.getValues().stream()
                .map(this::makeGoalRoomNodeDetailResponse)
                .toList();
    }

    private GoalRoomRoadmapNodeDetailDto makeGoalRoomNodeDetailResponse(final GoalRoomRoadmapNode node) {
        final Long roadmapNodeId = node.getRoadmapNodeId();
        final RoadmapNode roadmapNode = findRoadmapNodeById(roadmapNodeId);
        return new GoalRoomRoadmapNodeDetailDto(node.getId(), roadmapNode.getTitle(), roadmapNode.getContent(),
                makeRoadmapNodeImageUrls(roadmapNode), node.getStartDate(), node.getEndDate(), node.getCheckCount());
    }

    private RoadmapNode findRoadmapNodeById(final Long roadmapNodeId) {
        return roadmapNodeRepository.findById(roadmapNodeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵 노드입니다. roadmapNodeId = " + roadmapNodeId));
    }

    private List<String> makeRoadmapNodeImageUrls(final RoadmapNode roadmapNode) {
        return roadmapNode.getRoadmapNodeImages()
                .getValues()
                .stream()
                .map(it -> fileService.generateUrl(it.getServerFilePath(), HttpMethod.GET).toExternalForm())
                .toList();
    }
}
