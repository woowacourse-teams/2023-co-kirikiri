package co.kirikiri.goalroom.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomMembersTest {

    @Test
    void 입력받은_사용자를_골룸_사용자_중에서_찾는다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null, 1L);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null, 2L);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        final GoalRoomMember findGoalRoomMember = goalRoomMembers.findByMemberId(1L).get();

        // then
        assertThat(findGoalRoomMember).isEqualTo(goalRoomMember1);
    }

    @Test
    void 다음_리더가_될_사용자를_찾는다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null, 1L);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null, 2L);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        final GoalRoomMember nextLeader = goalRoomMembers.findNextLeader().get();

        // then
        assertThat(nextLeader).isEqualTo(goalRoomMember2);
    }

    @Test
    void 골룸_사용자의_수를_구한다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null, 1L);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null, 2L);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        final int size = goalRoomMembers.size();

        // then
        assertThat(size).isEqualTo(2);
    }

    @Test
    void 골룸_사용자에서_입렵받은_사용자를_제거한다() {
        // given
        final GoalRoomMember goalRoomMember1 = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null, 1L);
        final GoalRoomMember goalRoomMember2 = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null, 2L);

        final GoalRoomMembers goalRoomMembers = new GoalRoomMembers(List.of(goalRoomMember1, goalRoomMember2));

        // when
        goalRoomMembers.remove(goalRoomMember1);

        // then
        assertThat(goalRoomMembers)
                .usingRecursiveComparison()
                .isEqualTo(new GoalRoomMembers(List.of(goalRoomMember2)));
    }
}
