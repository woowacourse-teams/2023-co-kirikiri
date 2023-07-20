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

class GoalRoomTest {

    @Test
    void 골룸에_대기중인_인원수를_계산한다() {
        // given
        final GoalRoom goalRoom = new GoalRoom("goalroom", 10, GoalRoomStatus.RECRUITING,
                new RoadmapContent("content"));
        final Member member1 = new Member(new Identifier("identifier"),
                new EncryptedPassword(new Password("password1")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), new Nickname("nickname"), "010-1111-1111"));
        final Member member2 = new Member(new Identifier("identifier"),
                new EncryptedPassword(new Password("password1")),
                new MemberProfile(Gender.FEMALE, LocalDate.of(2023, 7, 20), new Nickname("nickname"), "010-1111-1111"));

        // when
        goalRoom.joinGoalRoom(GoalRoomRole.LEADER, member1);
        goalRoom.joinGoalRoom(GoalRoomRole.FOLLOWER, member2);

        // then
        assertThat(goalRoom.getCurrentPendingMemberCount()).isEqualTo(2);
    }
}
