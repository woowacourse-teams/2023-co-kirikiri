package co.kirikiri.goalroom.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.common.exception.UnexpectedDomainException;
import co.kirikiri.goalroom.domain.vo.GoalRoomName;
import co.kirikiri.goalroom.domain.vo.LimitedMemberCount;
import co.kirikiri.goalroom.domain.vo.Period;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomPendingMembersTest {

    @Test
    void 골룸의_리더를_찾는다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10), 1L, 1L,
                골룸_로드맵_노드들을_생성한다());

        // when
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(List.of(
                new GoalRoomPendingMember(1L, GoalRoomRole.LEADER, null, goalRoom, 1L),
                new GoalRoomPendingMember(2L, GoalRoomRole.FOLLOWER, null, goalRoom, 2L)
        ));

        // then
        assertThat(goalRoomPendingMembers.findGoalRoomLeaderId()).isEqualTo(1L);
    }

    @Test
    void 골룸의_리더가_없으면_예외가_발생한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10), 1L, 1L,
                골룸_로드맵_노드들을_생성한다());

        // when
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(List.of(
                new GoalRoomPendingMember(1L, GoalRoomRole.FOLLOWER, null, goalRoom, 1L),
                new GoalRoomPendingMember(2L, GoalRoomRole.FOLLOWER, null, goalRoom, 2L)
        ));

        // then
        assertThatThrownBy(() -> goalRoomPendingMembers.findGoalRoomLeaderId())
                .isInstanceOf(UnexpectedDomainException.class);
    }

    @Test
    void 입력받은_사용자를_골룸_사용자_중에서_찾는다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER, null,
                null, 1L);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(2L, GoalRoomRole.FOLLOWER, null,
                null, 2L);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2));

        // when
        final GoalRoomPendingMember findGoalRoomPendingMember = goalRoomPendingMembers.findByMemberId(1L).get();

        // then
        assertThat(findGoalRoomPendingMember).isEqualTo(goalRoomPendingMember1);
    }

    @Test
    void 다음_리더가_될_사용자를_찾는다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER,
                LocalDateTime.now(), null, 1L);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(1L, GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null, 2L);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2));

        // when
        final GoalRoomPendingMember nextLeader = goalRoomPendingMembers.findNextLeader().get();

        // then
        assertThat(nextLeader).isEqualTo(goalRoomPendingMember2);
    }

    @Test
    void 골룸_사용자의_수를_구한다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER,
                LocalDateTime.now(), null, 1L);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(1L, GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null, 2L);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2));

        // when
        final int size = goalRoomPendingMembers.size();

        // then
        assertThat(size).isEqualTo(2);
    }

    @Test
    void 골룸_사용자에서_입렵받은_사용자를_제거한다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(1L, GoalRoomRole.LEADER,
                LocalDateTime.now(), null, 1L);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(1L, GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null, 2L);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2));

        // when
        goalRoomPendingMembers.remove(goalRoomPendingMember1);

        // then
        assertThat(goalRoomPendingMembers)
                .usingRecursiveComparison()
                .isEqualTo(new GoalRoomPendingMembers(List.of(goalRoomPendingMember2)));
    }

    private GoalRoomRoadmapNodes 골룸_로드맵_노드들을_생성한다() {
        return new GoalRoomRoadmapNodes(List.of(
                new GoalRoomRoadmapNode(new Period(LocalDate.now(), LocalDate.now().plusDays(5)), 5, 1L))
        );
    }
}
