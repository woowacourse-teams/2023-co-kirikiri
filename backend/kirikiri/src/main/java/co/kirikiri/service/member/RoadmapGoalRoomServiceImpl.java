package co.kirikiri.service.member;

import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.roadmap.domain.Roadmap;
import co.kirikiri.roadmap.service.RoadmapGoalRoomService;
import co.kirikiri.service.aop.ExceptionConvert;
import co.kirikiri.service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@ExceptionConvert
public class RoadmapGoalRoomServiceImpl implements RoadmapGoalRoomService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;

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
        return !goalRoomRepository.findByRoadmap(roadmap).isEmpty();
    }
}
