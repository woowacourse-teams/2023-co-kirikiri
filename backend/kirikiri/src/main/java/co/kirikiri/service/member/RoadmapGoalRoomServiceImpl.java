package co.kirikiri.service.member;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.dto.RoadmapGoalRoomsOrderType;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.service.RoadmapGoalRoomService;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomNumberDto;
import co.kirikiri.roadmap.service.dto.RoadmapGoalRoomsOrderTypeDto;
import co.kirikiri.roadmap.service.dto.response.RoadmapGoalRoomResponses;
import co.kirikiri.service.FileService;
import co.kirikiri.service.aop.ExceptionConvert;
import co.kirikiri.service.dto.CustomScrollRequest;
import co.kirikiri.service.dto.goalroom.RoadmapGoalRoomDto;
import co.kirikiri.service.dto.goalroom.RoadmapGoalRoomScrollDto;
import co.kirikiri.service.dto.member.MemberDto;
import co.kirikiri.service.exception.BadRequestException;
import co.kirikiri.service.mapper.GoalRoomMapper;
import co.kirikiri.service.mapper.ScrollResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class RoadmapGoalRoomServiceImpl implements RoadmapGoalRoomService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final FileService fileService;

    @Override
    public Member findCompletedGoalRoomMember(final Long roadmapId, final String identifier) {
        return goalRoomMemberRepository.findByRoadmapIdAndMemberIdentifierAndGoalRoomStatus(roadmapId,
                        new Identifier(identifier), GoalRoomStatus.COMPLETED)
                .orElseThrow(() -> new BadRequestException(
                        "로드맵에 대해서 완료된 골룸이 존재하지 않습니다. roadmapId = " + roadmapId + " memberIdentifier = " + identifier))
                .getMember();
    }

    @Override
    public boolean hasGoalRooms(final Roadmap roadmap) {
        return !findGoalRoomsByRoadmap(roadmap).isEmpty();
    }

    private List<GoalRoom> findGoalRoomsByRoadmap(final Roadmap roadmap) {
        return goalRoomRepository.findByRoadmap(roadmap);
    }

    @Override
    public RoadmapGoalRoomNumberDto findRoadmapGoalRoomsByRoadmap(final Roadmap roadmap) {
        return GoalRoomMapper.convertRoadmapGoalRoomDto(findGoalRoomsByRoadmap(roadmap));
    }

    @Override
    public RoadmapGoalRoomResponses makeRoadmapGoalRoomResponsesByOrderType(final Roadmap roadmap, final RoadmapGoalRoomsOrderTypeDto orderTypeDto, final CustomScrollRequest scrollRequest) {
        final RoadmapGoalRoomsOrderType orderType = GoalRoomMapper.convertToGoalRoomOrderType(orderTypeDto);
        final List<RoadmapGoalRoomDto> roadmapGoalRoomDtos = goalRoomRepository.findGoalRoomsByRoadmapAndCond(roadmap, orderType, scrollRequest.lastId(), scrollRequest.size())
                .stream()
                .map(this::makeGoalRoomDto)
                .toList();
        final List<RoadmapGoalRoomDto> subDtos = ScrollResponseMapper.getSubResponses(roadmapGoalRoomDtos, scrollRequest.size());
        final boolean hasNext = ScrollResponseMapper.hasNext(roadmapGoalRoomDtos.size(), scrollRequest.size());

        return GoalRoomMapper.convertToRoadmapGoalRoomResponses(new RoadmapGoalRoomScrollDto(subDtos, hasNext));
    }

    private RoadmapGoalRoomDto makeGoalRoomDto(final GoalRoom goalRoom) {
        final Member goalRoomLeader = goalRoom.findGoalRoomLeader();
        return new RoadmapGoalRoomDto(goalRoom.getId(), goalRoom.getName().getValue(), goalRoom.getStatus(),
                goalRoom.getCurrentMemberCount(), goalRoom.getLimitedMemberCount().getValue(),
                goalRoom.getCreatedAt(), goalRoom.getStartDate(),
                goalRoom.getEndDate(), makeMemberDto(goalRoomLeader));
    }

    private MemberDto makeMemberDto(final Member creator) {
        final URL url = fileService.generateUrl(creator.getImage().getServerFilePath(), HttpMethod.GET);
        return new MemberDto(creator.getId(), creator.getNickname().getValue(), url.toExternalForm());
    }
}
