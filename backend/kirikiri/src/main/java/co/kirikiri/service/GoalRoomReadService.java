package co.kirikiri.service;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCheckFeedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomReadService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final CheckFeedRepository checkFeedRepository;

    public GoalRoomResponse findGoalRoom(final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        return GoalRoomMapper.convertGoalRoomResponse(goalRoom);
    }

    private GoalRoom findGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithRoadmapContent(goalRoomId)
                .orElseThrow(() -> new NotFoundException("골룸 정보가 존재하지 않습니다. goalRoomId = " + goalRoomId));
    }

    public GoalRoomCertifiedResponse findGoalRoom(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomById(goalRoomId);
        final boolean isJoined = isMemberGoalRoomJoin(new Identifier(identifier), goalRoom);
        return GoalRoomMapper.convertGoalRoomCertifiedResponse(goalRoom, isJoined);
    }

    private boolean isMemberGoalRoomJoin(final Identifier identifier, final GoalRoom goalRoom) {
        if (goalRoom.isRecruiting()) {
            return goalRoomPendingMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, identifier).isPresent();
        }
        return goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, identifier).isPresent();
    }

    public List<GoalRoomMemberResponse> findGoalRoomMembers(final Long goalRoomId) {
        final List<GoalRoomMember> goalRoomMembers = goalRoomMemberRepository.findByGoalRoomIdOrderByAccomplishmentRateDesc(
                goalRoomId);
        checkGoalRoomEmpty(goalRoomId, goalRoomMembers);
        return GoalRoomMapper.convertToGoalRoomMemberResponses(goalRoomMembers);
    }

    private void checkGoalRoomEmpty(final Long goalRoomId, final List<GoalRoomMember> goalRoomMembers) {
        if (goalRoomMembers.isEmpty()) {
            throw new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId);
        }
    }

    public List<GoalRoomCheckFeedResponse> findGoalRoomCheckFeeds(final String identifier, final Long goalRoomId) {
        final GoalRoom goalRoom = findGoalRoomWithNodesById(goalRoomId);
        validateJoinedMemberInRunningGoalRoom(goalRoom, identifier);
        final GoalRoomRoadmapNode currentGoalRoomRoadmapNode = goalRoom.getNodeByDate(LocalDate.now());
        final List<CheckFeed> checkFeeds = checkFeedRepository.findByGoalRoomRoadmapNodeWithGoalRoomMemberAndMemberImage(
                currentGoalRoomRoadmapNode);
        return GoalRoomMapper.convertToGoalRoomCheckFeedResponse(checkFeeds);
    }

    private GoalRoom findGoalRoomWithNodesById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithNodes(goalRoomId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId));
    }

    private void validateJoinedMemberInRunningGoalRoom(final GoalRoom goalRoom, final String identifier) {
        goalRoomMemberRepository.findByGoalRoomAndMemberIdentifier(goalRoom, new Identifier(identifier))
                .orElseThrow(() -> new BadRequestException("골룸에 참여하지 않은 회원입니다."));
    }
}
