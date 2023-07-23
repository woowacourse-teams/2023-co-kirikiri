package co.kirikiri.service;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.member.Member;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import co.kirikiri.service.dto.CustomPageRequest;
import co.kirikiri.service.dto.PageResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomForListResponse;
import co.kirikiri.service.dto.goalroom.response.GoalRoomResponse;
import co.kirikiri.service.mapper.GoalRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoalRoomService {

    // TODO : 추후에 GoalRoomMembers 관련 로직 추가 시 수정 필요
    private final MemberRepository memberRepository;
    private final GoalRoomRepository goalRoomRepository;

    public GoalRoomResponse findMemberGoalRoom(final Long memberId, final Long goalRoomId) {
        final Member member = findMember(memberId);
        return null;
    }

    public PageResponse<GoalRoomForListResponse> findMemberGoalRooms(final Long memberId,
                                                                     final CustomPageRequest pageRequest) {
        final Member member = findMember(memberId);
        final PageRequest generatedPageRequest = PageRequest.of(pageRequest.page(), pageRequest.size());
        final Page<GoalRoom> goalRoomsPage = goalRoomRepository.findGoalRoomsPageByMember(member, generatedPageRequest);

        return GoalRoomMapper.convertToGoalRoomsPageResponse(goalRoomsPage, pageRequest);
    }

    private Member findMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthenticationException("존재하지 않는 회원입니다."));
    }
}
