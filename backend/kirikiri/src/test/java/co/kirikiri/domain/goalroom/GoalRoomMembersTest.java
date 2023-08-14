package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomMembersTest {

    private static final Member MEMBER1 = new Member(1L, new Identifier("identifier1"),
            new EncryptedPassword(new Password("password1!")),
            new Nickname("name1"), null, null);
    private static final Member MEMBER2 = new Member(2L, new Identifier("identifier2"),
            new EncryptedPassword(new Password("password2!")),
            new Nickname("name2"), null, null);

    @Test
    void 입력받은_사용자를_골룸_사용자_중에서_찾는다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                MEMBER1);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null,
                MEMBER2);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        final GoalRoomMember findGoalRoomMember = goalRoomMembers.findByMember(MEMBER1).get();

        // then
        assertThat(findGoalRoomMember).isEqualTo(goalRoomMember1);
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
        final GoalRoomMember nextLeader = goalRoomMembers.findNextLeader().get();

        // then
        assertThat(nextLeader).isEqualTo(goalRoomMember2);
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
