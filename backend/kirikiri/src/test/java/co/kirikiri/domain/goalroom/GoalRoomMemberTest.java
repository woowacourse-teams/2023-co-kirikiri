package co.kirikiri.domain.goalroom;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

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
        final Member member = new Member(new Identifier("identifier"),
                new EncryptedPassword(new Password("password1!")),
                new Nickname("name"), null, null);
        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                member);

        // when
        final boolean result = goalRoomMember.isSameMember(member);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 입력받은_멤버가_자신과_다른_멤버이면_false를_반환한다() {
        // given
        final Member member1 = new Member(1L, new Identifier("identifier1"),
                null, new EncryptedPassword(new Password("password1!")),
                new Nickname("name1"), null, null);
        final Member member2 = new Member(2L, new Identifier("identifier2"),
                null, new EncryptedPassword(new Password("password2!")),
                new Nickname("name2"), null, null);

        final GoalRoomMember goalRoomMember = new GoalRoomMember(GoalRoomRole.LEADER, LocalDateTime.now(), null,
                member1);

        // when
        final boolean result = goalRoomMember.isSameMember(member2);

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
