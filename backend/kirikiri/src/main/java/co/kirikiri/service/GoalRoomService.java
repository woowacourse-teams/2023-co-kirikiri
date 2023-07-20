package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNodes;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.domain.roadmap.RoadmapNode;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.persistence.roadmap.RoadmapContentRepository;
import co.kirikiri.service.dto.goalroom.GoalRoomCreateDto;
import co.kirikiri.service.dto.goalroom.GoalRoomRoadmapNodeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomCreateRequest;
import co.kirikiri.service.mapper.GoalRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalRoomService {

    private final GoalRoomRepository goalRoomRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long create(final GoalRoomCreateRequest goalRoomCreateRequest, final String memberIdentifier) {
        final GoalRoomCreateDto goalRoomCreateDto = GoalRoomMapper.convertToGoalRoomCreateDto(goalRoomCreateRequest);
        final RoadmapContent roadmapContent = findRoadmapContent(goalRoomCreateDto.roadmapContentId());
        checkNodeSizeEqual(roadmapContent.nodesSize(), goalRoomCreateDto.goalRoomRoadmapNodeDtosSize());
        final GoalRoomRoadmapNodes goalRoomRoadmapNodes = makeGoalRoomRoadmapNodes(goalRoomCreateDto.goalRoomRoadmapNodeDtos(), roadmapContent);
        final GoalRoom goalRoom = new GoalRoom(goalRoomCreateDto.goalRoomName(), goalRoomCreateDto.limitedMemberCount(), roadmapContent);
        goalRoomRepository.save(goalRoom);
        final GoalRoomPendingMember goalRoomPendingMember = makeGoalRoomPendingMember(memberIdentifier, goalRoom);
        goalRoom.addAllGoalRoomRoadmapNodes(goalRoomRoadmapNodes);
        goalRoom.addGoalRoomTodo(goalRoomCreateDto.goalRoomToDo());
        goalRoom.participate(goalRoomPendingMember);
        return goalRoom.getId();
    }

    private RoadmapContent findRoadmapContent(final Long roadmapContentId) {
        return roadmapContentRepository.findById(roadmapContentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 로드맵입니다."));
    }

    private void checkNodeSizeEqual(final int nodesSize, final int goalRoomRoadmapNodeDtosSize) {
        if (nodesSize != goalRoomRoadmapNodeDtosSize) {
            throw new BadRequestException("모든 노드에 대해 기간이 설정 돼야합니다.");
        }
    }

    private GoalRoomRoadmapNodes makeGoalRoomRoadmapNodes(final List<GoalRoomRoadmapNodeDto> goalRoomRoadmapNodeDtos, final RoadmapContent roadmapContent) {
        final List<GoalRoomRoadmapNode> goalRoomRoadmapNodes = goalRoomRoadmapNodeDtos.stream()
                .map(it -> new GoalRoomRoadmapNode(it.startDate(), it.endDate(), it.checkCount(), findRoadmapNode(roadmapContent, it.roadmapNodeId())))
                .collect(Collectors.toList());
        return new GoalRoomRoadmapNodes(goalRoomRoadmapNodes);
    }

    private RoadmapNode findRoadmapNode(final RoadmapContent roadmapContent, final Long roadmapNodeId) {
        return roadmapContent.findRoadmapNodeById(roadmapNodeId)
                .orElseThrow(() -> new NotFoundException("로드맵에 존재하지 않는 노드입니다."));
    }

    private GoalRoomPendingMember makeGoalRoomPendingMember(final String memberIdentifier, final GoalRoom savedGoalRoom) {
        final Member member = findMember(memberIdentifier);
        return new GoalRoomPendingMember(GoalRoomRole.LEADER, savedGoalRoom, member);
    }

    private Member findMember(final String memberIdentifier) {
        return memberRepository.findByIdentifier(new Identifier(memberIdentifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }
}
