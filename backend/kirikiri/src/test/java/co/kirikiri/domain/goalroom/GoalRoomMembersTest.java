package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.ServerException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomMembersTest {

    private static final Member MEMBER1 = new Member(1L, new Identifier("identifier1"),
            new EncryptedPassword(new Password("password1!")),
            new Nickname("name1"), null);
    private static final Member MEMBER2 = new Member(2L, new Identifier("identifier2"),
            new EncryptedPassword(new Password("password2!")),
            new Nickname("name2"), null);

    @Test
    void 입력받은_사용자를_골룸_사용자_중에서_찾는다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                MEMBER1);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null,
                MEMBER2);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        final GoalRoomMember findGoalRoomMember = goalRoomMembers.findByMember(MEMBER1);

        // then
        assertThat(findGoalRoomMember).isEqualTo(goalRoomMember1);
    }

    @Test
    void 입력받은_사용자가_골룸에_존재하지_않으면_예외가_발생한다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                MEMBER1);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1));

        // when
        // then
        assertThatThrownBy(() -> goalRoomMembers.findByMember(MEMBER2))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("골룸에 참여한 사용자가 아닙니다. memberId = 2");
    }

    @Test
    void 다음_리더가_될_사용자를_찾는다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                MEMBER1);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null,
                MEMBER2);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        final GoalRoomMember nextLeader = goalRoomMembers.findNextLeader();

        // then
        assertThat(nextLeader).isEqualTo(goalRoomMember2);
    }

    @Test
    void 골룸의_참여자가_1명_이하이면_다음_리더가_될_사용자를_찾을때_예외가_발생한다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                MEMBER1);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1));

        // when
        // then
        assertThatThrownBy(() -> goalRoomMembers.findNextLeader())
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("골룸 참여자가 1명 이하이므로 다음 리더를 찾을 수 없습니다.");
    }

    @Test
    void 골룸_사용자의_수를_구한다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                MEMBER1);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null,
                MEMBER2);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        final int size = goalRoomMembers.size();

        // then
        assertThat(size).isEqualTo(2);
    }

    @Test
    void 골룸_사용자에서_입렵받은_사용자를_제거한다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                MEMBER1);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null,
                MEMBER2);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        goalRoomMembers.remove(goalRoomMember1);

        // then
        assertThat(goalRoomMembers)
                .usingRecursiveComparison()
                .isEqualTo(new GoalRoomMembers(List.of(goalRoomMember2)));
    }
}
