package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.dto.goalroom.GoalRoomResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    public GoalRoomResponse findGoalRoom(final Long goalRoomId) {
        final GoalRoom goalRoom = findById(goalRoomId);
        return GoalRoomMapper.convertGoalRoomResponse(goalRoom, null);
    }

    private GoalRoom findById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithRoadmapContent(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    public GoalRoomResponse findGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findById(goalRoomId);
        final boolean isJoined = isMemberGoalRoomJoin(new Identifier(identifier), goalRoom);
        return GoalRoomMapper.convertGoalRoomResponse(goalRoom, isJoined);
    }

    private boolean isMemberGoalRoomJoin(final Identifier identifier, final GoalRoom goalRoom) {
        return goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(identifier, goalRoom).isPresent();
    }
}
