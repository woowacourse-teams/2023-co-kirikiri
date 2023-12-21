package co.kirikiri.goalroom.domain;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class GoalRoomPendingMemberTest {

    @Test
    void 골룸의_리더이면_True를_반환한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(1L, new GoalRoomName("goalroom"), new LimitedMemberCount(10), 1L, 1L);

        // when
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER, null,
                goalRoom, 1L);

        // then
        assertThat(goalRoomPendingMember.isLeader()).isTrue();
    }

    @Test
    void 골룸의_리더가_아니면_false를_반환한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10), 1L, 1L);

        // when
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(2L, GoalRoomRole.FOLLOWER, null,
                goalRoom, 2L);

        // then
        assertThat(goalRoomPendingMember.isLeader()).isFalse();
    }

    @Test
    void 입력받은_멤버가_자신과_같은_멤버이면_true를_반환한다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER,
                LocalDateTime.now(), null, 1L);

        // when
        final boolean result = goalRoomPendingMember.isSameMember(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 입력받은_멤버가_자신과_다른_멤버이면_false를_반환한다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER,
                LocalDateTime.now(), null, 1L);

        // when
        final boolean result = goalRoomPendingMember.isSameMember(2L);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 팔로워가_리더로_변경된다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(1L, GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null, null);

        // when
        goalRoomPendingMember.becomeLeader();

        // then
        assertThat(goalRoomPendingMember.isLeader()).isTrue();
    }
}
