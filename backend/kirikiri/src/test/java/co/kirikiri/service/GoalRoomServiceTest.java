package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import co.kirikiri.domain.goalroom.GoalRoom;
import co.kirikiri.domain.goalroom.GoalRoomPendingMember;
import co.kirikiri.domain.goalroom.GoalRoomRole;
import co.kirikiri.domain.goalroom.GoalRoomStatus;
import co.kirikiri.domain.goalroom.LimitedMemberCount;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.AuthenticationException;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.persistence.goalroom.GoalRoomRepository;
import co.kirikiri.persistence.member.MemberRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalRoomServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @InjectMocks
    GoalRoomService goalRoomService;

    @Test
    void 골룸에_참가한다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다("identifier1", "시진이");
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, 20);
        final Member follower = 사용자를_생성한다("identifier2", "팔로워");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when
        goalRoomService.join("identifier2", 1L);

        //then
        assertThat(goalRoom.getCurrentMemberCount())
                .isEqualTo(2);
    }

    @Test
    void 골룸_참가_요청시_유효한_사용자_아이디가_아니면_예외가_발생한다() {
        //given
        when(memberRepository.findByIdentifier(any()))
                .thenThrow(new AuthenticationException("존재하지 않는 회원입니다."));

        //when, then
        assertThatThrownBy(() -> goalRoomService.join("identifier2", 1L))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 골룸_참가_요청시_유효한_골룸_아이디가_아니면_예외가_발생한다() {
        //given
        final Member follower = 사용자를_생성한다("identifier1", "팔로워");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenThrow(new NotFoundException("존재하지 않는 골룸입니다. goalRoomId = 1"));

        //when, then
        assertThatThrownBy(() -> goalRoomService.join("identifier1", 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 골룸입니다. goalRoomId = 1");
    }

    @Test
    void 골룸_참가_요청시_제한_인원이_가득_찼을_경우_예외가_발생한다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다("identifier1", "시진이");
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, 1);
        final Member follower = 사용자를_생성한다("identifier2", "팔로워");

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when, then
        assertThatThrownBy(() -> goalRoomService.join("identifier2", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("제한 인원이 꽉 찬 골룸에는 참여할 수 없습니다.");
    }

    @Test
    void 골룸_참가_요청시_모집_중이_아닌_경우_예외가_발생한다() {
        //given
        final RoadmapContent roadmapContent = new RoadmapContent("컨텐츠 본문");
        final Member creator = 사용자를_생성한다("identifier1", "시진이");
        final GoalRoom goalRoom = 골룸을_생성한다(creator, roadmapContent, 20);
        final Member follower = 사용자를_생성한다("identifier2", "팔로워");
        goalRoom.updateStatus(GoalRoomStatus.RUNNING);

        when(memberRepository.findByIdentifier(any()))
                .thenReturn(Optional.of(follower));
        when(goalRoomRepository.findById(anyLong()))
                .thenReturn(Optional.of(goalRoom));

        //when, then
        assertThatThrownBy(() -> goalRoomService.join("identifier2", 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("모집 중이지 않은 골룸에는 참여할 수 없습니다.");
    }

    private Member 사용자를_생성한다(final String identifier, final String nickname) {
        final MemberProfile memberProfile = new MemberProfile(Gender.MALE, LocalDate.of(1995, 9, 30),
                new Nickname(nickname), "010-1234-5678");

        return new Member(new Identifier(identifier),
                new EncryptedPassword(new Password("password1!")), memberProfile);
    }

    private GoalRoom 골룸을_생성한다(final Member creator, final RoadmapContent roadmapContent,
                              final Integer limitedMemberCount) {
        return new GoalRoom(
                "골룸 이름",
                new LimitedMemberCount(limitedMemberCount),
                roadmapContent,
                new GoalRoomPendingMember(creator, GoalRoomRole.LEADER)
        );
    }
}