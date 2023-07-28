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
import org.junit.jupiter.api.Test;

class GoalRoomPendingMemberTest {

    @Test
    void 골룸의_리더이면_True를_반환한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                new RoadmapContent("content"));
        final Member member = new Member(new Identifier("identifier"), new EncryptedPassword(new Password("password1")),
                new Nickname("nickname"), new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), "010-1111-1111"));

        // when
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.LEADER, goalRoom,
                member);

        // then
        assertThat(goalRoomPendingMember.isLeader()).isTrue();
    }

    @Test
    void 골룸의_리더가_아니면_false를_반환한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom(new GoalRoomName("goalroom"), new LimitedMemberCount(10),
                new RoadmapContent("content"));
        final Member member = new Member(new Identifier("identifier"), new EncryptedPassword(new Password("password1")),
                new Nickname("nickname"), new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), "010-1111-1111"));

        // when
        final GoalRoomPendingMember goalRoomPendingMember = new GoalRoomPendingMember(GoalRoomRole.FOLLOWER, goalRoom,
                member);

        // then
        assertThat(goalRoomPendingMember.isLeader()).isFalse();
    }
}
