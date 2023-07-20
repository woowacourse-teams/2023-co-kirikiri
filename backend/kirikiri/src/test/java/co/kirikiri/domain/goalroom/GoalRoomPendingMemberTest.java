package co.kirikiri.domain.goalroom;

import static org.assertj.core.api.Assertions.assertThat;

import co.kirikiri.domain.member.EncryptedPassword;
import co.kirikiri.domain.member.Gender;
import co.kirikiri.domain.member.Member;
import co.kirikiri.domain.member.MemberProfile;
import co.kirikiri.domain.member.vo.Identifier;
import co.kirikiri.domain.member.vo.Nickname;
import co.kirikiri.domain.member.vo.Password;
import co.kirikiri.domain.roadmap.RoadmapContent;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class GoalRoomPendingMemberTest {

    @Test
    void 골룸의_리더이면_True를_반환한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom("goalroom", 10, GoalRoomStatus.RECRUITING,
                new RoadmapContent("content"));
        final Member member = new Member(new Identifier("identifier"), new EncryptedPassword(new Password("password1")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), new Nickname("nickname"), "010-1111-1111"));

        // when
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom,
                member);

        // then
        assertThat(goalRoomPendingMember.isLeader()).isTrue();
    }

    @Test
    void 골룸의_리더가_아니면_false를_반환한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom("goalroom", 10, GoalRoomStatus.RECRUITING,
                new RoadmapContent("content"));
        final Member member = new Member(new Identifier("identifier"), new EncryptedPassword(new Password("password1")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), new Nickname("nickname"), "010-1111-1111"));

        // when
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom,
                member);

        // then
        assertThat(goalRoomPendingMember.isLeader()).isFalse();
    }
}
