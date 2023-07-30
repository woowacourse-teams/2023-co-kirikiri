package co.kirikiri.service;

import co.kirikiri.domain.goalroom.CheckFeed;
import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomRoadmapNode;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.CheckFeedRepository;
import co.kirikiri.persistence.goalroom.GoalRoomMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomPendingMemberRepository;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.goalroom.dto.GoalRoomFilterType;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.GoalRoomFilterTypeDto;
import co.kirikiri.service.dto.goalroom.request.GoalRoomStatusTypeRequest;
import co.kirikiri.service.dto.goalroom.response.GoalRoomCertifiedResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomForListResponse;
import co.kirikiri.service.dto.member.response.MemberGoalRoomResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomReadService {

    private final MemberRepository memberRepository;
    private final GoalRoomRepository goalRoomRepository;
    private final GoalRoomMemberRepository goalRoomMemberRepository;
    private final GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;
    private final CheckFeedRepository checkFeedRepository;

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

    public PageResponse<GoalRoomForListResponse> findGoalRoomsByFilterType(final GoalRoomFilterTypeDto filterTypeDto,
                                                                           final CustomPageRequest pageRequest) {
        final GoalRoomFilterType filterType = GoalRoomMapper.convertToGoalRoomFilterType(filterTypeDto);
        final PageRequest generatedPageRequest = PageRequest.of(pageRequest.page(), pageRequest.size());
        final Page<GoalRoom> goalRoomsWithPendingMembersPage = goalRoomRepository.findGoalRoomsWithPendingMembersPageByCond(
                filterType, generatedPageRequest);
        return GoalRoomMapper.convertToGoalRoomsPageResponse(goalRoomsWithPendingMembersPage, pageRequest);
    }

    public MemberGoalRoomResponse findMemberGoalRoom(final String identifier, final Long goalRoomId) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        final GoalRoom goalRoom = findMemberGoalRoomById(goalRoomId);
        final GoalRoomRoadmapNode goalRoomRoadmapNode = goalRoom.getNodeByDate(LocalDate.now());
        final List<CheckFeed> checkFeeds = checkFeedRepository.findByGoalRoomRoadmapNode(goalRoomRoadmapNode);

        return null;
    }

    private Member findMemberByIdentifier(final Identifier identifier) {
        return memberRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 회원입니다."));
    }

    private GoalRoom findMemberGoalRoomById(final Long goalRoomId) {
        return goalRoomRepository.findByIdWithContentAndNodesAndTodos(goalRoomId)
                .orElseThrow();
    }

    public List<MemberGoalRoomForListResponse> findMemberGoalRoomsByStatusType(final String identifier,
                                                                               final GoalRoomStatusTypeRequest goalRoomStatusTypeRequest) {
        final Member member = findMemberByIdentifier(new Identifier(identifier));
        return null;
    }
}