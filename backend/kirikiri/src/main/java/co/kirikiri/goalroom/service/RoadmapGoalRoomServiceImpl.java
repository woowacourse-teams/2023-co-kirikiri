package co.kirikiri.goalroom.service;

import co.kirikiri.common.aop.ExceptionConvert;
import co.kirikiri.common.exception.BadRequestException;
import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.common.mapper.ScrollResponseMapper;
import co.kirikiri.common.service.FileService;
import co.kirikiri.common.service.dto.CustomScrollRequest;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomRole;
import co.kirikiri.goalroom.domain.GoalRoomStatus;
import co.kirikiri.goalroom.persistence.GoalRoomMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.goalroom.persistence.dto.RoadmapGoalRoomsOrderType;
import co.kirikiri.goalroom.service.dto.MemberDto;
import co.kirikiri.goalroom.service.dto.RoadmapGoalRoomDto;
import co.kirikiri.goalroom.service.dto.RoadmapGoalRoomScrollDto;
import co.kirikiri.goalroom.service.mapper.GoalRoomMapper;
import co.kirikiri.member.domain.Member;
import co.kirikiri.member.domain.vo.Identifier;
import co.kirikiri.member.persistence.MemberRepository;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.domain.RoadmapContent;
import co.kirikiri.roadmap.persistence.RoadmapContentRepository;
import co.kirikiri.roadmap.service.RoadmapGoalRoomService;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomNumberDto;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomsOrderTypeDto;
import co.kirikiri.roadmap.service.dto.response.RoadmapGoalRoomResponses;
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
public class RoadmapGoalRoomServiceImpl implements RoadmapGoalRoomService {

    private static final int DELETE_AFTER_MONTH = 3;

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final RoadmapContentRepository roadmapContentRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @Override
    public Long findCompletedGoalRoomMemberId(final Long roadmapId, final String identifier) {
        final Member member = findMemberByIdentifier(identifier);
        return goalRoomMemberRepository.findByRoadmapIdAndMemberIdAndGoalRoomStatus(roadmapId, member.getId(),
                        GoalRoomStatus.COMPLETED)
                .orElseThrow(() -> new BadRequestException(
                        "로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = " + roadmapId + " memberIdentifier = " + identifier))
                .getMemberId();
    }

    private Member findMemberByIdentifier(final String identifier) {
        return memberRepository.findByIdentifier(new Identifier(identifier))
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    @Override
    public boolean hasGoalRooms(final Long roadmapId) {

        return !findGoalRoomsByRoadmapId(roadmapId).isEmpty();
    }

    private List<GoalRoom> findGoalRoomsByRoadmapId(final Long roadmapId) {
        final RoadmapContent roadmapContent = findRoadmapContentByRoadmapId(roadmapId);
        return goalRoomRepository.findByRoadmapContentId(roadmapContent.getId());
    }

    private RoadmapContent findRoadmapContentByRoadmapId(final Long roadmapId) {
        return roadmapContentRepository.findFirstByRoadmapIdOrderByCreatedAtDesc(roadmapId)
                .orElseThrow(() -> new NotFoundException("로드맵 본문이 존재하지 않습니다."));
    }

    @Override
    public RoadmapGoalRoomNumberDto findRoadmapGoalRoomsByRoadmap(final Roadmap roadmap) {
        return GoalRoomMapper.convertRoadmapGoalRoomDto(findGoalRoomsByRoadmapId(roadmap.getId()));
    }

    @Override
    public RoadmapGoalRoomResponses makeRoadmapGoalRoomResponsesByOrderType(final Long roadmapId,
                                                                            final RoadmapGoalRoomsOrderTypeDto orderTypeDto,
                                                                            final CustomScrollRequest scrollRequest) {
        final RoadmapGoalRoomsOrderType orderType = GoalRoomMapper.convertToGoalRoomOrderType(orderTypeDto);
        final RoadmapContent roadmapContent = findRoadmapContentByRoadmapId(roadmapId);
        final List<RoadmapGoalRoomDto> roadmapGoalRoomDtos = goalRoomRepository.findGoalRoomsByRoadmapContentIdAndCond(
                        roadmapContent.getId(), orderType, scrollRequest.lastId(), scrollRequest.size())
                .stream()
                .map(this::makeGoalRoomDto)
                .toList();
        final List<RoadmapGoalRoomDto> subDtos = ScrollResponseMapper.getSubResponses(roadmapGoalRoomDtos,
                scrollRequest.size());
        final boolean hasNext = ScrollResponseMapper.hasNext(roadmapGoalRoomDtos.size(), scrollRequest.size());

        return GoalRoomMapper.convertToRoadmapGoalRoomResponses(new RoadmapGoalRoomScrollDto(subDtos, hasNext));
    }

    private RoadmapGoalRoomDto makeGoalRoomDto(final GoalRoom goalRoom) {
        final Long goalRoomLeaderId = findGoalRoomLeaderId(goalRoom);
        return new RoadmapGoalRoomDto(goalRoom.getId(), goalRoom.getName().getValue(), goalRoom.getStatus(),
                getCurrentMemberCount(goalRoom), goalRoom.getLimitedMemberCount().getValue(),
                goalRoom.getCreatedAt(), goalRoom.getStartDate(),
                goalRoom.getEndDate(), makeMemberDto(goalRoomLeaderId));
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

    private int getCurrentMemberCount(final GoalRoom goalRoom) {
        if (goalRoom.isRecruiting()) {
            return goalRoomPendingMemberRepository.findByGoalRoom(goalRoom)
                    .size();
        }
        return goalRoomMemberRepository.findAllByGoalRoom(goalRoom)
                .size();
    }

    private MemberDto makeMemberDto(final Long creatorId) {
        final Member creator = findMemberById(creatorId);
        final URL url = fileService.generateUrl(creator.getImage().getServerFilePath(), HttpMethod.GET);
        return new MemberDto(creator.getId(), creator.getNickname().getValue(), url.toExternalForm());
    }

    private Member findMemberById(final Long creatorId) {
        return memberRepository.findById(creatorId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다. memberId = " + creatorId));
    }

    @Override
    @Transactional
    public boolean canDeleteGoalRoomsInRoadmap(final Long roadmapId) {
        final RoadmapContent roadmapContent = findRoadmapContentByRoadmapId(roadmapId);
        final List<GoalRoom> goalRooms = goalRoomRepository.findByRoadmapContentId(roadmapContent.getId());
        return canDeleteRoadmapBasedOnGoalRooms(goalRooms);
    }

    private boolean canDeleteRoadmapBasedOnGoalRooms(final List<GoalRoom> goalRooms) {
        return goalRooms.stream()
                .allMatch(goalRoom -> goalRoom.isCompleted() && goalRoom.isCompletedAfterMonths(DELETE_AFTER_MONTH));
    }
}
