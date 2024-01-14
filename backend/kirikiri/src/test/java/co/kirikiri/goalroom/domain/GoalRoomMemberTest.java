package co.kirikiri.goalroom.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class GoalRoomMemberTest {

    @Test
    void 골룸의_리더이면_true를_반환한다() {
        // given
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null, null);

        // when
        final boolean result = goalRoomMember.isLeader();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 골룸의_리더가_아니면_false를_반환한다() {
        // given
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null,
                null);

        // when
        final boolean result = goalRoomMember.isLeader();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 입력받은_멤버가_자신과_같은_멤버이면_true를_반환한다() {
        // given
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null, 1L);

        // when
        final boolean result = goalRoomMember.isSameMember(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 입력받은_멤버가_자신과_다른_멤버이면_false를_반환한다() {
        // given
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null, 1L);

        // when
        final boolean result = goalRoomMember.isSameMember(2L);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 팔로워가_리더로_변경된다() {
        // given
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.FOLLOWER, LocalDateTime.now(), null,
                null);

        // when
        goalRoomMember.becomeLeader();

        // then
        assertThat(goalRoomMember.isLeader()).isTrue();
    }
}
