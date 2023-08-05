package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.goalroom.vo.GoalRoomName;
import co.kirikiri.domain.goalroom.vo.LimitedMemberCount;
import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapContent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class GoalRoomPendingMemberTest {

    @Test
    void 골룸의_리더이면_True를_반환한다() {
        // given
        final Member member = new Member(new Identifier("identifier"), new EncryptedPassword(new Password("password1")),
                new Nickname("nickname"), new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), "010-1111-1111"));
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                new RoadmapContent("content"), member);

        // when
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom,
                member);

        // then
        assertThat(goalRoomPendingMember.isLeader()).isTrue();
    }

    @Test
    void 골룸의_리더가_아니면_false를_반환한다() {
        // given
        final Member member = new Member(new Identifier("identifier"), new EncryptedPassword(new Password("password1")),
                new Nickname("nickname"), new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), "010-1111-1111"));
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                new RoadmapContent("content"), member);

        // when
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom,
                member);

        // then
        assertThat(goalRoomPendingMember.isLeader()).isFalse();
    }

    @Test
    void 입력받은_멤버가_자신과_같은_멤버이면_true를_반환한다() {
        // given
        final Member member = new Member(new Identifier("identifier"),
                new EncryptedPassword(new Password("password1!")),
                new Nickname("name"), null);
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), null,
                member);

        // when
        final boolean result = goalRoomPendingMember.isSameMember(member);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 입력받은_멤버가_자신과_다른_멤버이면_false를_반환한다() {
        // given
        final Member member1 = new Member(1L, new Identifier("identifier1"),
                new EncryptedPassword(new Password("password1!")),
                new Nickname("name1"), null);
        final Member member2 = new Member(2L, new Identifier("identifier2"),
                new EncryptedPassword(new Password("password2!")),
                new Nickname("name2"), null);

        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER,
                LocalDateTime.now(), null,
                member1);

        // when
        final boolean result = goalRoomPendingMember.isSameMember(member2);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 팔로워가_리더로_변경된다() {
        // given
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER,
                LocalDateTime.now(), null,
                null);

        // when
        goalRoomPendingMember.becomeLeader();

        // then
        assertThat(goalRoomPendingMember.isLeader()).isTrue();
    }
}
