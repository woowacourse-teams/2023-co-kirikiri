package co.kirikiri.goalroom.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import co.kirikiri.common.exception.NotFoundException;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.goalroom.domain.GoalRoom;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNode;
import co.kirikiri.goalroom.domain.GoalRoomRoadmapNodes;
import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import co.kirikiri.goalroom.domain.vo.Period;
import co.kirikiri.goalroom.persistence.GoalRoomPendingMemberRepository;
import co.kirikiri.goalroom.persistence.GoalRoomRepository;
import co.kirikiri.goalroom.service.event.GoalRoomLeaderUpdateEvent;
import co.kirikiri.persistence.member.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoalRoomLeaderUpdateEventListenerTest {

    private static final Member MEMBER = new Member(1L, new Identifier("identifier1"),
            null, new EncryptedPassword(new Password("password1!")), new Nickname("닉네임"),
            null, new MemberProfile(Gender.FEMALE, "kirikiri@email.com"));

    @Mock
    private GoalRoomRepository goalRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private GoalRoomPendingMemberRepository goalRoomPendingMemberRepository;

    @InjectMocks
    private GoalRoomLeaderUpdateEventListener goalRoomLeaderUpdateEventListener;

    @Test
    void 정상적으로_골룸의_리더를_저장한다() {
        // given
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, 1L, 10);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));
        given(goalRoomRepository.findById(anyLong()))
                .willReturn(Optional.of(goalRoom));

        // when
        // then
        assertDoesNotThrow(() -> goalRoomLeaderUpdateEventListener.handleGoalRoomLeaderUpdate(
                new GoalRoomLeaderUpdateEvent(1L, "identifier1")));
    }

    @Test
    void 골룸_리더_저장_시_존재하지_않은_회원의_Identifier이면_예외를_던진다() {
        // given
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, 1L, 10);

        // when
        // then
        assertThatThrownBy(
                () -> goalRoomLeaderUpdateEventListener.handleGoalRoomLeaderUpdate(
                        new GoalRoomLeaderUpdateEvent(1L, "identifier2")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 골룸_리더_저장_시_존재하지_않은_골룸이면_예외를_던진다() {
        // given
        final GoalRoom goalRoom = 골룸을_생성한다(1L, MEMBER, 1L, 10);

        given(memberRepository.findByIdentifier(any()))
                .willReturn(Optional.of(MEMBER));

        // when
        // then
        assertThatThrownBy(
                () -> goalRoomLeaderUpdateEventListener.handleGoalRoomLeaderUpdate(
                        new GoalRoomLeaderUpdateEvent(2L, "identifier1")))
                .isInstanceOf(NotFoundException.class);
    }

    private GoalRoom 골룸을_생성한다(final Long goalRoomId, final Member creator, final Long roadmapContentId,
                              final Integer limitedMemberCount) {
        return new GoalRoom(goalRoomId, new GoalRoomName("골룸 이름"), new LimitedMemberCount(limitedMemberCount),
                roadmapContentId, 골룸_로드맵_노드들을_생성한다());
    }

    private GoalRoomRoadmapNodes 골룸_로드맵_노드들을_생성한다() {
        return new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(LocalDate.now(), LocalDate.now().plusDays(10)), 5, 1L))
        );
    }
}
