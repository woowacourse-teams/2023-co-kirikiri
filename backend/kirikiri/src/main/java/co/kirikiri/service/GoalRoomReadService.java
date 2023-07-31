package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomMember;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.dto.GoalRoomFilterType;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomMemberResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomReadService {

    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

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

    public PageResponse<GoalRoomForListResponse> findGoalRoomsByFilterType(final GoalRoomFilterTypeDto filterTypeDto,
                                                                           final CustomPageRequest pageRequest) {
        final GoalRoomFilterType filterType = GoalRoomMapper.convertToGoalRoomFilterType(filterTypeDto);
        final PageRequest generatedPageRequest = PageRequest.of(pageRequest.page(), pageRequest.size());
        final Page<GoalRoom> goalRoomsWithPendingMembersPage = goalRoomRepository.findGoalRoomsWithPendingMembersPageByCond(
                filterType, generatedPageRequest);
        return GoalRoomMapper.convertToGoalRoomsPageResponse(goalRoomsWithPendingMembersPage, pageRequest);
    }

    public List<GoalRoomMemberResponse> findGoalRoomMembers(final Long goalRoomId) {
        final List<GoalRoomMember> goalRoomMembers = goalRoomMemberRepository.findByGoalRoomIdOrderByAccomplishmentRateDesc(goalRoomId);
        checkGoalRoomEmpty(goalRoomId, goalRoomMembers);
        return GoalRoomMapper.convertToGoalRoomMemberResponses(goalRoomMembers);
    }

    private void checkGoalRoomEmpty(final Long goalRoomId, final List<GoalRoomMember> goalRoomMembers) {
        if (goalRoomMembers.isEmpty()) {
            throw new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = " + goalRoomId);
        }
    }
}
