package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.exception.UnexpectedDomainException;
import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.roadmap.domain.RoadmapContent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoalRoomPendingMembersTest {

    private static final Member MEMBER1 = new Member(1L, new Identifier("identifier1"),
            null, new EncryptedPassword(new Password("password1!")),
            new Nickname("name1"), null, null);
    private static final Member MEMBER2 = new Member(2L, new Identifier("identifier2"),
            null, new EncryptedPassword(new Password("password2!")),
            new Nickname("name2"), null, null);

    @Test
    void 골룸의_리더를_찾는다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                new RoadmapContent("content", 1L), MEMBER1);

        // when
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(List.of(
                new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom, MEMBER2),
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, MEMBER1)
        ));

        // then
        assertThat(goalRoomPendingMembers.findGoalRoomLeader()).isEqualTo(MEMBER2);
    }

    @Test
    void 골룸의_리더가_없으면_예외가_발생한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                new RoadmapContent("content", 1L), MEMBER1);

        // when
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(List.of(
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, MEMBER1),
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, MEMBER2)
        ));

        // then
        assertThatThrownBy(() -> assertThat(goalRoomPendingMembers.findGoalRoomLeader()))
                .isInstanceOf(UnexpectedDomainException.class);
    }

    @Test
    void 입력받은_사용자를_골룸_사용자_중에서_찾는다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), null, MEMBER1);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null, MEMBER2);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2));

        // when
        final GoalRoomPendingMember findGoalRoomPendingMember = goalRoomPendingMembers.findByMember(MEMBER1).get();

        // then
        assertThat(findGoalRoomPendingMember).isEqualTo(goalRoomPendingMember1);
    }

    @Test
    void 다음_리더가_될_사용자를_찾는다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), null,
                MEMBER1);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null,
                MEMBER2);

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
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), null,
                MEMBER1);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null,
                MEMBER2);

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
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), null,
                MEMBER1);
        final GoalRoomPendingMember goalRoomPendingMember2 = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null,
                MEMBER2);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                List.of(goalRoomPendingMember1, goalRoomPendingMember2));

        // when
        goalRoomPendingMembers.remove(goalRoomPendingMember1);

        // then
        assertThat(goalRoomPendingMembers)
                .usingRecursiveComparison()
                .isEqualTo(new GoalRoomPendingMembers(List.of(goalRoomPendingMember2)));
    }
}
