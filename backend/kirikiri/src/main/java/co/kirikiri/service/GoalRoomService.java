package co.kirikiri.service;

import co.kirikiri.domain.goalroom.*;
import co.kirikiri.domain.goalroom.vo.Period;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomService {

    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final MemberRepository memberRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

    @Transactional
    public Long create(final GoalRoomCreateRequest goalRoomCreateRequest, final String memberIdentifier) {
        final GoalRoomCreateDto goalRoomCreateDto = GoalRoomMapper.convertToGoalRoomCreateDto(goalRoomCreateRequest);
        final RoadmapContent roadmapContent = findRoadmapContentById(goalRoomCreateDto.roadmapContentId());
        checkNodeSizeEqual(roadmapContent.nodesSize(), goalRoomCreateDto.goalRoomRoadmapNodeDtosSize());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = makeGoalRoomRoadmapNodes(goalRoomCreateDto.goalRoomRoadmapNodeDtos(), roadmapContent);
        final GoalRoom goalRoom = new GoalRoom(goalRoomCreateDto.goalRoomName(), goalRoomCreateDto.limitedMemberCount(), roadmapContent);
        final GoalRoomPendingMember goalRoomPendingMember = makeGoalRoomPendingMember(memberIdentifier);
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoom.addGoalRoomTodo(goalRoomCreateDto.goalRoomToDo());
        goalRoom.participate(goalRoomPendingMember);
        return goalRoomRepository.save(goalRoom).getId();
    }

    private RoadmapContent findRoadmapContentById(final Long roadmapContentId) {
        return roadmapContentRepository.findById(roadmapContentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다."));
    }

    private void checkNodeSizeEqual(final int roadmapNodesSize, final int goalRoomRoadmapNodeDtosSize) {
        if (roadmapNodesSize != goalRoomRoadmapNodeDtosSize) {
            throw new BadRequestException("모든 노드에 대해 기간이 설정돼야 합니다.");
        }
    }

    private GoalRoomRoadmapNodes makeGoalRoomRoadmapNodes(final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos, final RoadmapContent roadmapContent) {
        final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes = goalRoomRoadmapNodeDtos.stream()
                .map(it -> makeGoalRoomRoadmapNode(roadmapContent, it))
                .toList();
        return new GoalRoomRoadmapNodes(goalRoomRoadmapNodes);
    }

    private GoalRoomRoadmapNode makeGoalRoomRoadmapNode(final RoadmapContent roadmapContent, final GoalRoomRoadmapNodeDto it) {
        return new GoalRoomRoadmapNode(new Period(it.startDate(), it.endDate()), it.checkCount(), findRoadmapNode(roadmapContent, it.roadmapNodeId()));
    }

    private RoadmapNode findRoadmapNode(final RoadmapContent roadmapContent, final Long roadmapNodeId) {
        return roadmapContent.findRoadmapNodeById(roadmapNodeId)
                .orElseThrow(() -> new NotFoundException("로드맵에 존재하지 않는 노드입니다."));
    }

    private GoalRoomPendingMember makeGoalRoomPendingMember(final String memberIdentifier) {
        final Member member = findMemberByIdentifier(memberIdentifier);
        return new GoalRoomPendingMember(GoalRoomRole.LEADER, member);
    }

    private Member findMemberByIdentifier(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    public GoalRoomResponse findGoalRoom(final Long goalRoomId) {
        final GoalRoom goalRoom = findById(goalRoomId);
        return GoalRoomMapper.convertGoalRoomResponse(goalRoom);
    }

    private GoalRoom findById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithRoadmapContent(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    public GoalRoomCertifiedResponse findGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findById(goalRoomId);
        final boolean isJoined = isMemberGoalRoomJoin(new Identifier(identifier), goalRoom);
        return GoalRoomMapper.convertGoalRoomCertifiedResponse(goalRoom, isJoined);
    }

    private boolean isMemberGoalRoomJoin(final Identifier identifier, final GoalRoom goalRoom) {
        if (goalRoom.isRecruiting()) {
            return goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, identifier).isPresent();
        }
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, identifier).isPresent();
    }
}
