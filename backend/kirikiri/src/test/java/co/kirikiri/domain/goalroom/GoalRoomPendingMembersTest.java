package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapContent;
import co.kirikiri.exception.BadRequestException;
import co.kirikiri.exception.NotFoundException;
import co.kirikiri.exception.ServerException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class GoalRoomPendingMembersTest {

    private static final Member MEMBER1 = new Member(1L, new Identifier("identifier1"),
            new EncryptedPassword(new Password("password1!")),
            new Nickname("name1"), null);
    private static final Member MEMBER2 = new Member(2L, new Identifier("identifier2"),
            new EncryptedPassword(new Password("password2!")),
            new Nickname("name2"), null);

    @Test
    void 골룸의_리더를_찾는다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                new RoadmapContent("content"), MEMBER1);

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
                new RoadmapContent("content"), MEMBER1);

        // when
        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(List.of(
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, MEMBER1),
                new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom, MEMBER2)
        ));

        // then
        assertThatThrownBy(() -> assertThat(goalRoomPendingMembers.findGoalRoomLeader()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 입력받은_사용자를_골룸_사용자_중에서_찾는다() {
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
        final GoalRoomPendingMember findGoalRoomPendingMember = goalRoomPendingMembers.findByMember(MEMBER1);

        // then
        assertThat(findGoalRoomPendingMember).isEqualTo(goalRoomPendingMember1);
    }

    @Test
    void 입력받은_사용자가_골룸에_존재하지_않으면_예외가_발생한다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), null,
                MEMBER1);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                List.of(goalRoomPendingMember1));

        // when
        // then
        assertThatThrownBy(() -> goalRoomPendingMembers.findByMember(MEMBER2))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("골룸에 참여한 사용자가 아닙니다. memberId = 2");
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
        final GoalRoomPendingMember nextLeader = goalRoomPendingMembers.findNextLeader();

        // then
        assertThat(nextLeader).isEqualTo(goalRoomPendingMember2);
    }

    @Test
    void 골룸의_참여자가_1명_이하이면_다음_리더가_될_사용자를_찾을때_예외가_발생한다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember1 = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), null,
                MEMBER1);

        final GoalRoomPendingMembers goalRoomPendingMembers = new GoalRoomPendingMembers(
                List.of(goalRoomPendingMember1));

        // when
        // then
        assertThatThrownBy(() -> goalRoomPendingMembers.findNextLeader())
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("골룸 참여자가 1명 이하이므로 다음 리더를 찾을 수 없습니다.");
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
